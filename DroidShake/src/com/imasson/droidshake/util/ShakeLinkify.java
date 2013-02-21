package com.imasson.droidshake.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.text.util.Linkify.MatchFilter;
import android.text.util.Linkify.TransformFilter;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;

/**
 * <p>提供文字内超链接识别功能的工具类</p>
 * <p>本类为{@link TextView}和{@link Spannable}服务，为其附加指定类型的超链接识别功能。</br>
 * 本类以{@link Linkify}为基础构建，并增强了部分正则表达式和识别功能。</p>
 * 
 * @see android.text.util.Linkify
 * @see android.util.Patterns
 * @see ShakePatterns
 */
public class ShakeLinkify {
    private static final String TAG = "ShakeLinkify";

    /**
     * <p>为指定的一段文字添加超链接识别，该操作将清除原来加在文字上的超链接</p>
     * <p>注意：该段文字必须是{@link Spannable}，如果不是的话，
     *建议先使用{@link SpannableString#valueOf}进行高效的转换。</p>
     * 
     * @param text  需要添加超链接识别的文字
     * @param mask  超链接类型的标志位，请参考{@link Linkify}
     * @return 是否对这段文字进行了修改
     * @see Linkify#addLinks(Spannable, int)
     */
    public static final boolean addLinks(Spannable text, int mask) {
        if(text == null) {
            Log.w(TAG, "Argument 'text' is null on addLinks(Spannable, int)!");
            return false;
        }
        
        if (mask == 0) {
            return false;
        }

        URLSpan[] old = text.getSpans(0, text.length(), URLSpan.class);

        for (int i = old.length - 1; i >= 0; i--) {
            text.removeSpan(old[i]);
        }

        ArrayList<LinkSpec> links = new ArrayList<LinkSpec>();

        if ((mask & Linkify.WEB_URLS) != 0) {
            // 此处使用了自己的网址识别的正则表达式，同时加入了FTP支持
            gatherLinks(links, text, ShakePatterns.WEB_URL,
                new String[] { "http://", "https://", "rtsp://" },
                Linkify.sUrlMatchFilter, null);
        }

        if ((mask & Linkify.EMAIL_ADDRESSES) != 0) {
            gatherLinks(links, text, ShakePatterns.EMAIL_ADDRESS,
                new String[] { "mailto:" },
                null, null);
        }

        if ((mask & Linkify.PHONE_NUMBERS) != 0) {
            gatherLinks(links, text, ShakePatterns.PHONE,
                new String[] { "tel:" },
                Linkify.sPhoneNumberMatchFilter, Linkify.sPhoneNumberTransformFilter);
        }

        if ((mask & Linkify.MAP_ADDRESSES) != 0) {
            gatherMapLinks(links, text);
        }

        pruneOverlaps(links);

        if (links.size() == 0) {
            return false;
        }

        for (LinkSpec link: links) {
            applyLink(link.url, link.start, link.end, text);
        }

        return true;
    }


    /**
     * <p>为指定的{@link TextView}添加超链接识别，该操作将清除原来加在文字上的超链接</p>
     * <p>注意：为了提高效率，推荐使用{@link #addLinks(Spannable, int)}进行识别，再设置显示该段文字。
     *但这样需要手动设置{@link LinkMovementMethod}，否则文本中的链接则无法点击。</p>
     * <p>如果直接使用该方法的话，一旦发现链接就会添加该Method，因此不需要考虑该问题了。</p>
     * 
     * @param text 需要添加超链接识别的文字
     * @param mask 超链接类型的标志位，请参考{@link Linkify}
     * @return 是否对TextView内的文字进行了修改
     * @see Linkify#addLinks(TextView, int)
     */
    public static final boolean addLinks(TextView text, int mask) {
        if(text == null) {
            Log.w(TAG, "Argument 'text' is null on addLinks(TextView, int)!");
            return false;
        }
        if (mask == 0) {
            return false;
        }

        CharSequence t = text.getText();

        if (t instanceof Spannable) {
            if (addLinks((Spannable) t, mask)) {
                addLinkMovementMethod(text);
                return true;
            }

            return false;
        } else {
            SpannableString s = SpannableString.valueOf(t);

            if (addLinks(s, mask)) {
                addLinkMovementMethod(text);
                text.setText(s);

                return true;
            }

            return false;
        }
    }
    
    private static final void addLinkMovementMethod(TextView t) {
        MovementMethod m = t.getMovementMethod();

        if ((m == null) || !(m instanceof LinkMovementMethod)) {
            if (t.getLinksClickable()) {
                t.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }
    }
    
    private static final void gatherLinks(ArrayList<LinkSpec> links,
            Spannable s, Pattern pattern, String[] schemes,
            MatchFilter matchFilter, TransformFilter transformFilter) {
        Matcher m = pattern.matcher(s);

        while (m.find()) {
            int start = m.start();
            int end = m.end();

            if (matchFilter == null || matchFilter.acceptMatch(s, start, end)) {
                LinkSpec spec = new LinkSpec();
                String url = makeUrl(m.group(0), schemes, m, transformFilter);
                
                spec.url = url;
                spec.start = start;
                spec.end = end;

                links.add(spec);
            }
        }
    }
    
    private static final void gatherMapLinks(ArrayList<LinkSpec> links, Spannable s) {
        String string = s.toString();
        String address;
        int base = 0;

        while ((address = WebView.findAddress(string)) != null) {
            int start = string.indexOf(address);

            if (start < 0) {
                break;
            }

            LinkSpec spec = new LinkSpec();
            int length = address.length();
            int end = start + length;
            
            spec.start = base + start;
            spec.end = base + end;
            string = string.substring(end);
            base += end;

            String encodedAddress = null;

            try {
                encodedAddress = URLEncoder.encode(address,"UTF-8");
            } catch (UnsupportedEncodingException e) {
                continue;
            }

            spec.url = "geo:0,0?q=" + encodedAddress;
            links.add(spec);
        }
    }
    
	private static final void applyLink(String url, int start, int end,
			Spannable text) {
		URLSpan span = new URLSpan(url);
		text.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	}

    private static final String makeUrl(String url, String[] prefixes,
            Matcher m, TransformFilter filter) {
        if (filter != null) {
            url = filter.transformUrl(m, url);
        }

        boolean hasPrefix = false;
        
        for (int i = 0; i < prefixes.length; i++) {
            if (url.regionMatches(true, 0, prefixes[i], 0,
                                  prefixes[i].length())) {
                hasPrefix = true;

                // Fix capitalization if necessary
                if (!url.regionMatches(false, 0, prefixes[i], 0,
                                       prefixes[i].length())) {
                    url = prefixes[i] + url.substring(prefixes[i].length());
                }

                break;
            }
        }

        if (!hasPrefix) {
            url = prefixes[0] + url;
        }

        return url;
    }
    
    private static final void pruneOverlaps(ArrayList<LinkSpec> links) {
        Comparator<LinkSpec>  c = new Comparator<LinkSpec>() {
            public final int compare(LinkSpec a, LinkSpec b) {
                if (a.start < b.start) {
                    return -1;
                }

                if (a.start > b.start) {
                    return 1;
                }

                if (a.end < b.end) {
                    return 1;
                }

                if (a.end > b.end) {
                    return -1;
                }

                return 0;
            }

            public final boolean equals(Object o) {
                return false;
            }
        };

        Collections.sort(links, c);

        int len = links.size();
        int i = 0;

        while (i < len - 1) {
            LinkSpec a = links.get(i);
            LinkSpec b = links.get(i + 1);
            int remove = -1;

            if ((a.start <= b.start) && (a.end > b.start)) {
                if (b.end <= a.end) {
                    remove = i + 1;
                } else if ((a.end - a.start) > (b.end - b.start)) {
                    remove = i + 1;
                } else if ((a.end - a.start) < (b.end - b.start)) {
                    remove = i;
                }

                if (remove != -1) {
                    links.remove(remove);
                    len--;
                    continue;
                }

            }

            i++;
        }
    }
    
    
	// Do not create this static utility class.
    private ShakeLinkify() {}
}

class LinkSpec {
    String url;
    int start;
    int end;
}