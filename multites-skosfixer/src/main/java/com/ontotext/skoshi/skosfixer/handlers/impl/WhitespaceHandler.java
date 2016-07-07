package com.ontotext.skoshi.skosfixer.handlers.impl;

import com.ontotext.skoshi.skosfixer.handlers.StatementHandler;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;

import java.util.Queue;

public class WhitespaceHandler implements StatementHandler {

	@Override
	public void beforeHandle() {
	}

	@Override
	public void handle(Queue<Statement> queue) {
		assert (queue.size() == 1);

		Statement st = queue.peek();

		Resource subject = st.getSubject();
		URI predicate = st.getPredicate();
		Value object = st.getObject();
		boolean changed = false;
		if (subject instanceof URI) {
			subject = new URIImpl(subject.stringValue().replace("%20", "+"));
			changed = true;
		}
		if (object instanceof URI) {
			object = new URIImpl(object.stringValue().replace("%20", "+"));
			changed = true;
		}
		if (changed) {
			queue.poll();
			queue.add(new StatementImpl(subject, predicate, object));
		}
	}

	@Override
	public void afterHandle() {
	}

}
