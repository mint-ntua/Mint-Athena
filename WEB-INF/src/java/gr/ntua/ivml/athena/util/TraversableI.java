package gr.ntua.ivml.athena.util;

import java.util.List;

public interface TraversableI {
	public List<? extends TraversableI> getChildren();
}
