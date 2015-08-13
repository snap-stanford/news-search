package edu.stanford.snap.spinn3rHadoop.classifiers;

import edu.stanford.snap.spinn3rHadoop.utils.Spinn3rDocument;

public interface Classifier {
	String getClass(Spinn3rDocument doc);
}
