package edu.stanford.snap.spinn3rHadoop.classifiers;

import junit.framework.Assert;

import org.junit.Test;

import edu.stanford.snap.spinn3rHadoop.utils.ContentCleaner;
import edu.stanford.snap.spinn3rHadoop.utils.Spinn3rDocument;

public class TextVsSpamClassifierTest {
	
	@Test
	public void testNumberOfSentence() {
		TextVsSpamClassifier classifier = new TextVsSpamClassifier();
		Assert.assertEquals(3, classifier.getNumberOfSentences("First sencetence. Second Sentence. Third sentence."));
		Assert.assertEquals(1, classifier.getNumberOfSentences("First sencetence"));
		Assert.assertEquals(1, classifier.getNumberOfSentences("First sencetence..."));
	}
	
	@Test
	public void testNumberOfWords() {
		TextVsSpamClassifier classifier = new TextVsSpamClassifier();
		Assert.assertEquals(6, classifier.getNumberOfWords("First sencetence. Second Sentence. Third sentence."));
		Assert.assertEquals(2, classifier.getNumberOfWords("First sencetence"));
	}
	
	@Test
	public void testRelativeNumberOfStopwords() {
		TextVsSpamClassifier classifier = new TextVsSpamClassifier();
		Assert.assertEquals(0.4, classifier.getRelativeNumberOfStopwords("This is a great day."));
		Assert.assertEquals(0.0, classifier.getRelativeNumberOfStopwords("First"));
	}
	
	@Test
	public void testGetClass() {
		TextVsSpamClassifier classifier = new TextVsSpamClassifier();
		Spinn3rDocument doc = new Spinn3rDocument("I	2011063021_00135488_W\nV	C\nS	en	0.999996\nG	true	0.33357193987115247	\nU	http://n0w6fr6a.blog.fc2.com/blog-entry-42.html\nD	2011-06-30 21:42:41\nT	????????????? ???????????????????\nC	 It has to be a spam.");
		Assert.assertEquals(TextVsSpamClassifier.SPAM, classifier.getClass(doc));
		
		doc = new Spinn3rDocument("I	2008090301_00024745_W\nV	A\nS	en	0.999997\nG	false	1.0	\nU	http://blog.myspace.com/index.cfm	2008-09-03 01:42:55\nT	myspace.com blogs - liz's rant time - liz myspace blog\nC	 spam spam spam spam spam spam spam spam spam spam spam spam spam spam spam spam spam spam spam spam spam spam spam spam spam spam spam spam spam spam spam spam spam spam spam spam spam spam spam spam spam spam spam spam spam spam spam spam spam spam spam spam spam spam");
		Assert.assertEquals(TextVsSpamClassifier.SPAM, classifier.getClass(doc));
		
		doc = new Spinn3rDocument("I	2008090301_00024745_W\nV	A\nS	en	0.999997\nG	false	1.0	\nU	http://blog.myspace.com/index.cfm	2008-09-03 01:42:55\nT	myspace.com blogs - liz's rant time - liz myspace blog\nC	 This should be a spam, but is not becuase of and could this could this about about about about about about about about about about about about about about about about about about about about about about about about about about about about about about about about about about about about about about about about about about about about about about about about about about about about about about about about about about about about about about about about about about about about about about about about about about");
		Assert.assertEquals(TextVsSpamClassifier.TEXT, classifier.getClass(doc));
		
		doc = new Spinn3rDocument("I	2010051400_00080606_W\nV	A\nS	en	0.999997\nG	false	1.0	\nU	http://party.thebamboozle.com/xn/detail/2622523:BlogPost:106194\nD	2010-05-14 00:12:16\nT	cheap exelon no script, best online price for exelon - the bamboozle music festivals\nC	 q: why are generic drugs cheaper than the brand name ones? cheap exelon no script exelon sale! price on exelon in wi edgar exelon online overnight buy exelon with no rx in wi where can i purchase exelon without prescription exelon online discount cheap buy exelon amex online without usa buy no prescription exelon buy online securely exelon saturday delivery exelon cod order cheap exelon no rx buy cheap exelon online now order exelon paypal without prescription at wi exelon next day no prescriptionordering exelon without a script buy exelon with no prescription at wisconsin buy cheap exelon fast online where to buy legitimate exelon online order exelon online without prescription at wisconsin keshena order exelon cod fedex how to buy exelon on line buy on-line exelon where can i purchase exelon on line buying exelon buy exelon without a prescription overnight shipping cheap exelon for sale with no prescription required buy exelon online without a prescription and no membership exelon order a prepaid mastercard buy com lvivhost online exelon buy exelon no rx offshore exelon online at wisconsin buy exelon no prescriptions at wisconsin exelon online fedex cod free consult exelon online without presciption buy exelon money buy buy online exelon rxs ordering exelon without prescription cheap exelon free fedex shipping overnight delivery of exelon buy exelon overnight free delivery in wisconsin exelon orderd online without prescription how to buy exelon free consultation order exelon without a prescription overnight delivery at wi buy exelon over the counter online on line cheap exelon for sale in wi buy exelon without a usa overnight shipping generic exelon online in wisconsin exelon no prescription required exelon online in wi buy exelon in uk discount exelon on line cod pay exelon buy cheap generic exelon online order exelon cheapest possible prices exelon purchased online without usa exelon next day delivery cheapest place buy exelon online purchase exelon next day delivery exelon no prescription usa fedex shipping cod saturday exelon buy exelon online overnight exelon with no perscription and delivered over night at wisconsin where to buy discount exelon online no script in wisconsin night fedex exelon exelon cod no prescription in wisconsin elroy overnight exelon without a usa buy cheap exelon no rx cheap not expensive order usa exelon ordering exelon over the counter for saleexelon shipped cash on no prescription exelon cash on delivery at wi exelon no prescription buy online can i buy exelon online in wi kimberly buy exelon online at cheap price exelon next day delivery cod cheap exelon free consultation exelon no prescription cod purchase exelon for over night delivery best buy online sale exelon cheap exelon next day shipping exelon online not expensive next day delivery exelon with no script insurance online buy exelon at wi buy exelon next day delivery at wi overnight delivery of exelon with no perscription buy exelon credit card buy exelon online no prescription buy exelon medication cod exelon to buy exelon with free dr consultation best place to buy exelon online how to buy exelon online with overnight delivery overnight exelon buy exelon amex online ordering exelon online without a prescription at wi woodland order exelon no visa without rx how to get a exelon no prescription exelon orders cod exelon cod saturday delivery online overnight shipping exelon buy cheap online uk exelon buy exelon overnight delivery in wisconsin buy cheap exelon in the uk order prescription free exelon images of exelon buy no prescription exelon at wi exelon with no perscription purchase exelon cod shipping at wi buy cheap exelon no usa buy exelon no doctor in wisconsin order exelon online cod buy exelon online order now buy exelon cheap in wisconsin lake delton order exelon online with overnight delivery buy exelon overnight cheap purchase exelon uk delivery& cheap exelon in the uk in wisconsin buy exelon mastercard how to buy exelon no rx cheap in wisconsin exelon on line cash on delivery buy exelon overnight shipping get exelon over the counter fedex order exelon with no prescription exelon no prescription exelon shipped cod on saturday delivery buy exelon for cash on delivery buy exelon cheap cod exelon doctor consult at wisconsin cheap online pharmacy exelon at wi buy exelon uk at discounted prices exelon buy buy exelon cheap without prescription buy exelon cheap overnight cheap exelon next day order exelon cheap buy com online phentermine exelon in wi exelon discount fedex no rx in wisconsin exelon online doctors exelon with saturday delivery in wisconsin luck exelon online prescriptions with no membership");
		Assert.assertEquals(TextVsSpamClassifier.SPAM, classifier.getClass(doc));
		
		doc = new Spinn3rDocument("I	2008090301_00024745_W\nV	A\nS	en	0.999997\nG	false	1.0	\nU	http://blog.myspace.com/in	2008-09-03 01:42:55\nT	myspace.com blogs - liz's rant time - liz myspace blog\nC	 would you consider pride to be a fault or a virtue? gender : female status : single angry alright i have to rant and get this out. so tonight i'm in the lounge plaing pool and one of the guys comes in and turns on the republican convention. ok kool, i'm a republican so this is good. then my ra comes in and he's talking to her and starts bashing the republicans that we're stupid and christianity this and that and we have horrible beliefs etc. i finally completely insulted just said nicely that i was a republican and he didn't even let me finish when he just dove right in again saying he can't believe there's so much of us on campus and we should leave this isn't the college for us cause we're this that and the other thing. i finally just finished my game and left not wanting to say the words in my head cause not only was one a swear word but i really just wanted to dive into the whole, \"i thought this was a diverse place were no matter what you believe you're excepted and that no one has the right to put down your beliefs.\" the wort part was my ra did nothing except say i love you so it doesn't matter (to me p.s.) what you believe. pissed...that's lame compared to how i feel. you don't sit there and demand people to respect your beliefs when you sit there and dis others to their face even.");
		Assert.assertEquals(TextVsSpamClassifier.TEXT, classifier.getClass(doc));
	}
}
