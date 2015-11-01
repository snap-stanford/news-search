package edu.stanford.snap.spinn3rHadoop.utils;

import junit.framework.Assert;

import org.junit.Test;

public class ContentCleanerTest {
	
	@Test
	public void testCssCleaner() {
		ContentCleaner cc = new ContentCleaner();
		Assert.assertEquals(cc.cssCleaner("test text"), "test text");
		
		Assert.assertEquals(cc.cssCleaner("body{backgroundcolor: lightblue;}h1 {    color: navy;    margin-left: 20px;}"), "");
		
		Assert.assertEquals(cc.cssCleaner("pre text "
				+ "input{border: 1px solid; border-color: #c0c0c0 #ededed #ededed #c0c0c0;padding:2px 0px 2px 1px;font-size:1.0em;vertical-align:middle;color:#000;background:#fff;} "
				+ "textarea{border: 1px solid; border-color: #c0c0c0 #ededed #ededed #c0c0c0;background:#fff;}"
				+ "post text"), "pre text  post text");

	}
	
	@Test
	public void testCharactersCleaner() {
		ContentCleaner cc = new ContentCleaner();
		Assert.assertEquals(cc.charactersCleaner("test text"), "test text");
		Assert.assertEquals(cc.charactersCleaner("??? ??? ???????"), "");
		Assert.assertEquals(cc.charactersCleaner("what is this text about!?? knowbody knows!"), "what is this text about!?? knowbody knows!");
		Assert.assertEquals(cc.charactersCleaner("One more test????? ???? ???? ab??b ab????a"), "One more test ab??b aba");
	}
	
	@Test
	public void testHtmlCleaner() {
		ContentCleaner cc = new ContentCleaner();
		Assert.assertEquals(cc.htmlCleaner("test text"), "test text");
		
		Assert.assertEquals(cc.htmlCleaner("\" size=\"39\" onclick=\"highlight(getelementbyid('html-code')); apitrack('copy_image_html');\" /> enviar mediante correo electrónico "), 
										   "\"  enviar mediante correo electrónico ");
		Assert.assertEquals(cc.htmlCleaner("new: craziest celebrity twitter pics\" width=\"101\" height=\"56\" /> new: craziest celebrity twitter pics\">\""), 
										   "new: craziest celebrity twitter pics\"  new: craziest celebrity twitter pics\"\"");
		Assert.assertEquals(cc.htmlCleaner("her own . meta itemprop = `` datePublished '' content = `` 2014-02-05 17:50:38 '' > Mass"), 
				   						   "her own . Mass");
		
		Assert.assertEquals(cc.htmlCleaner("her own . meta itemprop = `` datePublished '' content = `` 2014-02-05 17:50:38 '' > Mass her own . meta itemprop = `` datePublished '' content = `` 2014-02-05 17:50:38 '' > Mass"), 
				   						   "her own . Mass her own . Mass");
		
	}
	
	@Test
	public void testWordpressCleaner() {
		ContentCleaner cc = new ContentCleaner();
		Assert.assertEquals(cc.wordpressCleaner("test text"), "test text");
		Assert.assertEquals(cc.wordpressCleaner("allowed to go on. [ youtube={jumble of numbers after = in url} ] i thought"), 
												"allowed to go on.  i thought");
		
		Assert.assertEquals(cc.wordpressCleaner("[1234] text [text2]"), " text ");
	}
	
	@Test
	public void testCleaner() {
		Spinn3rDocument doc = new Spinn3rDocument("I	2010051403_00197641_W\nV	A\nS	en	0.999995\nG	false	1.0	\nU	http://keepyourchips.com/forums/25-Tournament-Poker/164581-9-man-STT-versus-9-man-st/1348131-Well-one-difference-that-\nD	2010-05-14 03:29:23\nT	post on tournament poker: 9-man stt versus 9-man steps\nC	 well one difference that i would like to add is that in steps you're likely to go higher in buyin than in a regular sng. for example i started with 30 step 1s, i got a step 5, a couple of step 4s, a couple of step 3, and a couple of step 2s right now. my problem is what to do with that step 5 for example. by itself it is worth more than 2 times what i've invested so far. and while it's profitable in the long run to shove 23 in certain situations for example, for me (a person that is not playing 216$ sngs/steps regularly) what is the most profitable thing to do in these marginal situations?");
		String docContent = doc.content;
		int numberChanged = ContentCleaner.cleanContent(doc);
		Assert.assertEquals(0, numberChanged);
		Assert.assertEquals(docContent, doc.content);
		
		doc = new Spinn3rDocument("I	2010080100_00204606_W\nV	C\nS	en	0.999996\nG	false	0.9994367783722895	\nU	http://imstarving.tumblr.com/post/886115752\nD	2010-07-31 23:36:59\nT	i'm starving! - #cosign rt @pearlfeckt10 i'm starving\nC	 i'm starving! - #cosign rt @pearlfeckt10 i'm starving body { /*margin: 0px;*/ background: url( repeat; font-family: 'lucida grande', helvetica, sans-serif; } #content { width: 420px; margin: auto; padding: 15px; background-color: #fff; position: relative; } a { color: #c00; } h1 { padding: 30px 0px 50px 0px; margin: 0px; text-align: center; font: bold 55px 'arial black', tahoma, helvetica, sans-serif; letter-spacing: -2px; line-height: 50px; } h1 a { color: #444; text-decoration: none; } #description { position: absolute; left: 465px; } #description div { font: normal 18px helvetica,sans-serif; line-height: 20px; width: 150px; color: #ffffff; } #description div#search { text-align: right; } #description div a { color: #ffffff; } #description #nav_container { font-size: 13px; font-weight: bold; } #description #nav_container .dim { filter: alpha(opacity=50); -moz-opacity: 0.5; opacity: 0.5; } #searchresultcount { margin: 0 0 30px; text-align: center; } .post { position: relative; margin-bottom: 40px; } .post div.labels { position: absolute; right: 435px; text-align: right; width: 150px; } .post div.date { background-color: #ccc; white-space: nowrap; font: normal 20px helvetica, sans-serif; letter-spacing: -1px; color: #fff; display: inline; padding: 3px 5px 0px 5px; line-height: 20px; } .post div.date a { color: #fff; text-decoration: none; } .post h2 { font-size: 18px; font-weight: bold; color: #c00; letter-spacing: -1px; margin: 0px 0px 10px 0px; } .post h2 a { color: #c00; text-decoration: none; } /* regular post */ .post .regular { font-size: 12px; color: #444; line-height: 17px; } .post .regular blockquote { font-style: italic; } /* photo post */ .post .photo img { border: solid 10px #eee; } .post .photo div.caption { font-size: 11px; color: #444; margin-top: 5px; } .post .photo div.caption a { color: #444; } /* quote post */ .post .quote span.quote { font: bold 28px helvetica, sans-serif; letter-spacing: -1px; color: #666; } .post .quote span.quote a { color: #666; } .post .quote span.quote big.quote { font: bold 60px georgia, serif; line-height: 8px; vertical-align: -20px; } .post .quote span.source { font-size: 16px; font-weight: bold; color: #444; letter-spacing: -1px; } .post .quote span.source a { color: #444; } /* link post */ .post .link a.link { font: bold 20px helvetica, sans-serif; letter-spacing: -1px; color: #c00; } .post .link span.description { font-size: 13px; font-weight: normal; letter-spacing: -1px; } /* conversation post */ .post .conversation ul { background-color: #f8f8f8; list-style-type: none; margin: 0px; padding: 0px; border-left: solid 5px #ddd; } .post .conversation ul li { border-bottom: solid 1px #ddd; font-size: 12px; padding: 4px 0px 4px 8px; color: #444; } .post .conversation ul li span.label { font-weight: bold; color: #111; } /* audio post */ .post .audio div.caption { font-size: 11px; color: #444; margin-top: 5px; } .post .audio div.caption a { color: #444; } /* video post */ .post .video { width: 400px; margin: auto; } .post .video div.caption { font-size: 11px; color: #444; margin-top: 5px; } .post div.video div.caption a { color: #444; } /* footer */ #footer { margin: 40px 0px 30px 0px; text-align: center; font-size: 12px; } #footer a { text-decoration: none; color: #444; } #footer a:hover { text-decoration: underline; } .query { font-weight: bold; } i'm starving! i'm fat oh, fuck you! what is love? omfblog no shit? stupid ventures archive / rss july 31 #cosign rt @pearlfeckt10 i'm starving \nL	0		http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\nL	1		http://www.w3.org/1999/xhtml\nL	55		http://28.media.tumblr.com/avatar_de045fd64b66_16.png\nL	55		http://imstarving.tumblr.com/rss\nL	95		http://www.stupidventures.com/tumblrfiles/frenchfries.jpg)");
		docContent = doc.content;
		numberChanged = ContentCleaner.cleanContent(doc);
		Assert.assertFalse(numberChanged == 0);
		Assert.assertFalse(docContent.equals(doc.content));
		
		doc = new Spinn3rDocument("I	2011063021_00135488_W\nV	C\nS	en	0.999996\nG	true	0.33357193987115247	\nU	http://n0w6fr6a.blog.fc2.com/blog-entry-42.html\nD	2011-06-30 21:42:41\nT	????????????? ???????????????????\nC	 ????????????? ??????????????????? ?????? ????? ????????????? ??????????????????? ??????????????????? 1,480? 300? 980? ---- ?????tv ?????????????????????????????????????????????????????????????????????????????????????????????????????? ? ?????????????????? ? ????? ???????? ?? sm special 11 sm? leg fuck ????????? i???????? ??? ?????????????? ???? ????? ?????????? ?????? ?????????????? ?????????the??1 ????? ?????????????? ??????? ?????? ?????? ???? ? 18??? / 18??? ???01? ???02? ???03? ???04? ???05? ? ????????? ? ????? ???????? ?? ???????? ?????? - ?????????????????????????????[20? eroe??? - ???????????????????????????[18? ????????r18 - ??????????????????1000?????? 2011/07/01(?) 06:47:14 | ??? | ???????:0 | ????:0 | ??? | ????7???? ?????100?16?? ???? ??????? ??: ????: ???????: url: ??: ?????: ???????: ????????????? ??????? ??????? url ??????????????(fc2???????) ?????? author:n0w6fr6a ???????????? ???? ??????????????????? (07/01) ????7???? ?????100?16?? (06/30) ?????????? ???? (06/29) ?? ?????????????????????????? (06/28) ??????? ??????????? vol.3 (06/27) ?????? ????????? ??????? 2011/07 (1) 2011/06 (29) 2011/05 (6) 2009/09 (6) ???? ??? (42) ?????? rss?????? ?????rss ???????rss ??????????rss ??? ???? ?????????????? powered by fc2??? ?????????? ??????????? ????? fc2??? ???? powered by fc2??? . copyright ©????????????? all rights reserved ");
		docContent = doc.content;
		numberChanged = ContentCleaner.cleanContent(doc);
		Assert.assertEquals(0, numberChanged);
		Assert.assertEquals(docContent, doc.content);
		
		numberChanged = ContentCleaner.cleanContent(doc, true);
		Assert.assertFalse(numberChanged == 0);
		Assert.assertFalse(docContent.equals(doc.content));
	}
	
	
}
