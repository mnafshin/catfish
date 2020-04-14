package org.dice_research.opal.catfish.service.impl;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.dice_research.opal.catfish.Catfish;
import org.dice_research.opal.catfish.service.Cleanable;

import java.util.LinkedList;
import java.util.List;

/**
 * Cleans structural contents, e.g. empty values.
 *
 * @author Adrian Wilke
 */
public class StructuralCleaner implements Cleanable {

    protected final Catfish catfish;

    public StructuralCleaner(Catfish catfish) {
        if (catfish == null) {
            throw new NullPointerException();
        }
        this.catfish = catfish;
    }

    @Override
    public void clean(Model model) {
        StmtIterator stmtIterator = model.listStatements();
        List<Statement> statementsToRemove = new LinkedList<>();
        while (stmtIterator.hasNext()) {
            Statement statement = stmtIterator.next();

            // Collect empty literals
            if (catfish.isRemovingEmptyLiterals() && statement.getObject().isLiteral()) {
                if (statement.getObject().asLiteral().getString().trim().isEmpty()) {
                    statementsToRemove.add(statement);
                }
            }

            // Collect empty blank nodes
            else if (catfish.isRemovingEmptyBlankNodes() && statement.getObject().isAnon()) {
                if (!statement.getObject().asResource().listProperties().hasNext()) {
                    statementsToRemove.add(statement);
                }
            }
        }

        // Remove from model
        model.remove(statementsToRemove);
    }
}