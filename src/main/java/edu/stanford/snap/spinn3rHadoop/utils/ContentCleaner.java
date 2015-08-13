package edu.stanford.snap.spinn3rHadoop.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContentCleaner {
	private static final int HTML_WINDOW_SIZE = 150;
	private static final String HTML_PATTERN1 = "[^\\s]+\\s{0,2}=\\s{0,2}([\"\\']).*\\1";
	private static final String HTML_PATTERN2 = "\\Wkeytype\\W|\\Wcols\\W|\\Wdatetime\\W|\\Wdisabled\\W|\\Waccept-charset\\W|\\Wshape\\W|\\Wcode\\W|\\Wusemap\\W|\\Walt\\W|\\Wstyle\\W|\\Wspellcheck\\W|\\Wtitle\\W|\\Wcontrols\\W|\\Whidden\\W|\\Wnovalidate\\W|\\Wcontenteditable\\W|\\Wname\\W|\\Wsizes\\W|\\Wlist\\W|\\Wbgcolor\\W|\\Wsummary\\W|\\Wcoords\\W|\\Wasync\\W|\\Walign\\W|\\Wdir\\W|\\Wpubdate\\W|\\Witemprop\\W|\\Wismap\\W|\\Wdownload\\W|\\Wsrclang\\W|\\Wmanifest\\W|\\Woptimum\\W|\\Wpattern\\W|\\Wselected\\W|\\Wlabel\\W|\\Wcontent\\W|\\Wrel\\W|\\Wmethod\\W|\\Wpreload\\W|\\Wcodebase\\W|\\Wstep\\W|\\Wscoped\\W|\\Wspan\\W|\\Wplaceholder\\W|\\Wsrcdoc\\W|\\Wsrc\\W|\\Wlanguage\\W|\\Wmaxlength\\W|\\Waction\\W|\\Wsrcset\\W|\\Wtabindex\\W|\\Wbuffered\\W|\\Wcolor\\W|\\Wcolspan\\W|\\Waccesskey\\W|\\Wautosave\\W|\\Wheight\\W|\\Whref\\W|\\Wwrap\\W|\\Wopen\\W|\\Wsize\\W|\\Wrows\\W|\\Wchecked\\W|\\Wwidth\\W|\\Wseamless\\W|\\Wstart\\W|\\Wlow\\W|\\Wscope\\W|\\Wenctype\\W|\\Wtype\\W|\\Wcite\\W|\\Wform\\W|\\Wposter\\W|\\Wreversed\\W|\\Wformaction\\W|\\Wradiogroup\\W|\\Wkind\\W|\\Wtarget\\W|\\Wdefault\\W|\\Wchallenge\\W|\\Wvalue\\W|\\Wautocomplete\\W|\\Wheaders\\W|\\Wloop\\W|\\Wdefer\\W|\\Wdirname\\W|\\Waccept\\W|\\Whreflang\\W|\\Whigh\\W|\\Wborder\\W|\\Wrowspan\\W|\\Wmin\\W|\\Wmedia\\W|\\Wcharset\\W|\\Wping\\W|\\Wreadonly\\W|\\Wautofocus\\W|\\Wmultiple\\W|\\Wmax\\W|\\Whttp-equiv\\W|\\Wdata\\W|\\Wclass\\W|\\Wicon\\W|\\Wlang\\W|\\Wcontextmenu\\W|\\Wrequired\\W|\\Wdraggable\\W|\\Wsandbox\\W|\\Wdropzone\\W|\\Wautoplay\\W|\\Wbgsound\\W|\\Wdel\\W|\\Wmeter\\W|\\Wins\\W|\\Wmeta\\W|\\Wtable\\W|\\Wfont\\W|\\Wcolgroup\\W|\\Wselect\\W|\\Wstyle\\W|\\Wimg\\W|\\Warea\\W|\\Wmenu\\W|\\Wtbody\\W|\\Wparam\\W|\\Wli\\W|\\Wsource\\W|\\Whtml\\W|\\Wdetails\\W|\\Wprogress\\W|\\Wtd\\W|\\Wtfoot\\W|\\Wbody\\W|\\Wmap\\W|\\WNone\\W|\\Wblockquote\\W|\\Wfieldset\\W|\\Woption\\W|\\Wform\\W|\\Wtrack\\W|\\Wobject\\W|\\Wcanvas\\W|\\Wthead\\W|\\Wbase\\W|\\Wlink\\W|\\Winput\\W|\\Woptgroup\\W|\\Wbasefont\\W|\\Whr\\W|\\Wol\\W|\\Wtextarea\\W|\\Wmarquee\\W|\\Wbutton\\W|\\Wscript\\W|\\Wlabel\\W|\\Wkeygen\\W|\\Wcaption\\W|\\Wapplet\\W|\\Wcommand\\W|\\Wiframe\\W|\\Wtime\\W|\\Woutput\\W|\\Wembed\\W|\\Waudio\\W|\\Wcol\\W";
	
	public String cssCleaner(String text) {
		return text.replaceAll("(\\w+)?(\\s*>\\s*)?(#\\w+)?\\s*(\\.\\w+)?\\s*\\{(.+?:.+?)\\}" , "");
	}
	
	public String charactersCleaner(String text) {
		text = text.replaceAll("\\?\\s\\?" , "??");
		return text.replaceAll("\\?{3,}", "");
	}
	
	public String htmlCleaner(String text) {
		if (text.indexOf('>') < 0) 
			return text;
		
		String[] textSplited = text.split(">");
		for (int i = 0; i < textSplited.length; i++) {
			int beginIndex = (textSplited[i].length() > HTML_WINDOW_SIZE) ? textSplited[i].length() - HTML_WINDOW_SIZE : 0;
			String htmlString = textSplited[i].substring(beginIndex);
			
			Pattern pattern = Pattern.compile(HTML_PATTERN1);
			Matcher matcher = pattern.matcher(htmlString);
			if (matcher.find()) {
				textSplited[i] = textSplited[i].substring(0, beginIndex) + textSplited[i].substring(beginIndex, matcher.start());
			}
			else {
				pattern = Pattern.compile(HTML_PATTERN2);
				matcher = pattern.matcher(htmlString);
				if (matcher.find()) {
					textSplited[i] = textSplited[i].substring(0, beginIndex) + textSplited[i].substring(beginIndex, matcher.start());
				}
			}
		}
		
		String finalText = "";
		for (String part: textSplited) {
			finalText += part;
		}
		return finalText;
	}
	
	public String wordpressCleaner(String text) {
		return text.replaceAll("\\[.{1,100}?\\]", "");
	}
	
	public static int cleanContent(Spinn3rDocument doc, boolean includeCharacters) {
		String content = doc.content;
		
		ContentCleaner cc = new ContentCleaner();
		if (includeCharacters) {
			content = cc.charactersCleaner(content);
		}
		content = cc.cssCleaner(content);
		content = cc.htmlCleaner(content);
		content = cc.wordpressCleaner(content);
		
		int numberOfChangedCharacters = doc.content.length() - content.length();
		doc.setContent(content);;
		
		return numberOfChangedCharacters;
	}
	
	public static int cleanContent(Spinn3rDocument doc) {
		return cleanContent(doc, false);
	}
}
