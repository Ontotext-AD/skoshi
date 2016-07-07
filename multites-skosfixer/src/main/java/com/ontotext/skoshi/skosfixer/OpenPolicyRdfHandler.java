package com.ontotext.skoshi.skosfixer;

import com.ontotext.skoshi.skosfixer.handlers.StatementHandler;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.SKOS;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.Rio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.io.Writer;
import java.util.*;

/**
 * OpenPolicy's concrete Sesame RDFHandler implementation.
 */
public class OpenPolicyRdfHandler implements RDFHandler {

	private final Logger log = LoggerFactory.getLogger(OpenPolicyRdfHandler.class);

	private final RDFHandler handler;

	private Collection<StatementHandler> transformers;
	private boolean started = false;

	private final Queue<Statement> queue = new LinkedList<>();

	public OpenPolicyRdfHandler(OutputStream outputStream, RDFFormat outputFormat) {
		handler = Rio.createWriter(outputFormat, outputStream);
		log.info("Initialized handler: " + handler);
	}

	public OpenPolicyRdfHandler(Writer writer, RDFFormat outputFormat) {
		handler = Rio.createWriter(outputFormat, writer);
		log.info("Initialized handler: " + handler);
	}

	/**
	 * The implementation is based on a Queue of statements.
	 * Each additional handler pops a statement from the queue
	 * and pushes the transformed statement or number of statements
	 * required to express the change.
	 */
	@Override
	public void handleStatement(Statement st) throws RDFHandlerException {

		queue.add(st);
		for (StatementHandler transformer : this.transformers) {
			transformer.handle(queue);
		}

		while (!queue.isEmpty()) {
			st = queue.poll();
			handler.handleStatement(st);
		}
	}

	/**
	 * This is a setter method, suitable for Spring usage
	 */
	public void setTransformers(Collection<StatementHandler> transformers) {
		if (!started) {
			this.transformers = transformers;
		} else {
			throw new IllegalStateException("Can not set transformers after the transformation has started.");
		}
	}

	public Collection<StatementHandler> getTransformers() {
		if (transformers == null) {
			this.transformers = Collections.emptyList();
		}
		return transformers;
	}

	@Override
	public void startRDF() throws RDFHandlerException {
		started = true;
		// relax some checks in handleStatement
		for (StatementHandler transformer : getTransformers()) {
			transformer.beforeHandle();
		}
		handler.startRDF();
	}

	@Override
	public void endRDF() throws RDFHandlerException {
		// Add the timestamp
		Calendar now = GregorianCalendar.getInstance();
		String date = now.get(Calendar.YEAR) + "-" + now.get(Calendar.MONTH) + "-" + now.get(Calendar.DATE);
		ValueFactory valueFactory = ValueFactoryImpl.getInstance();
		URI schemeSubject = valueFactory.createURI(SKOS.NAMESPACE + "OpenPolicyScheme");
		URI createdOnPredicate = valueFactory.createURI(SKOS.NAMESPACE + "createdOn");
		Literal dateObject = valueFactory.createLiteral(date);
		Statement st = new StatementImpl(schemeSubject, createdOnPredicate, dateObject);
		// write the statement in the file
		handler.handleStatement(st);

		for (StatementHandler transformer : getTransformers()) {
			transformer.afterHandle();
		}

		handler.endRDF();
	}

	@Override
	public void handleNamespace(String prefix, String uri)
			throws RDFHandlerException {
		handler.handleNamespace(prefix, uri);
	}

	@Override
	public void handleComment(String comment) throws RDFHandlerException {
		handler.handleComment(comment);
	}

}
