package com.ontotext.openpolicy.skosfixer.handlers.impl;

import com.ontotext.openpolicy.skosfixer.handlers.StatementHandler;
import com.ontotext.skoshi.rdf.SKOSX;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.BooleanLiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.vocabulary.SKOS;

import java.util.Queue;

public class StemmingHandler implements StatementHandler {

	/**
	 * Transforms the multites hack we implemented:  skos:note 'L'
     * into the correct triple  ?com.ontotext.skoshi.model.concept skos:stemming false .
	 */
	@Override
	public void handle(Queue<Statement> queue) {

        assert (queue.size() == 1);

		Statement st = queue.peek();

		if (st.getPredicate().equals(SKOS.NOTE) && "L".equals(st.getObject().stringValue())) {
            System.out.println("FIXING STEMMING FOR: " + st);
            queue.poll();
			queue.add(new StatementImpl(st.getSubject(), SKOSX.STEMMING, new BooleanLiteralImpl(false)));
		}
	}

	@Override
	public void beforeHandle() {
	}

	@Override
	public void afterHandle() {
	}

}
