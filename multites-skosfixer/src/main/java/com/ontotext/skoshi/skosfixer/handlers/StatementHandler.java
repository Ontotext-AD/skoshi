package com.ontotext.skoshi.skosfixer.handlers;

import org.openrdf.model.Statement;

import java.util.Queue;

/**
 * A handler to handle statements as they are read
 * from the broken rdf.
 */
public interface StatementHandler {

	/**
	 * This method is called only once,
	 * before the actual transformation of the
	 * results starts.
	 */
	void beforeHandle();

	/**
	 * fixes the statement if needed and returns the updated statement.
	 *
	 * @param queue The statement that potentially will be fixed
	 * @return the fixed statement ( to be assigned to the statement variable
	 * in the place, where the method is called)
	 */
	void handle(Queue<Statement> queue);

	/**
	 * This method is called only once,
	 * after the actual transformation of the
	 * results ends.
	 */
	void afterHandle();
}
