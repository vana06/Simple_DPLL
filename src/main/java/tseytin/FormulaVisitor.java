package tseytin;

import cnf.CNF;
import cnf.Disjunction;
import antlr.generated.CNFBaseVisitor;
import antlr.generated.CNFParser;
import util.IDPool;

import java.util.ArrayList;
import java.util.List;

public class FormulaVisitor extends CNFBaseVisitor<Integer> {
    private List<Disjunction> disjunctions;
    private IDPool pool = new IDPool();
    private Integer currentNewVarNumber = 0;

    public CNF parse(CNFParser.CnfContext ctx) {
        disjunctions = new ArrayList<>();
        visit(ctx);
        return new CNF(disjunctions);
    }

    @Override
    public Integer visitNot(CNFParser.NotContext ctx) {
        Integer node = visitChildren(ctx.cnf());
        Integer additionalVar = pool.idByName("@" + (++currentNewVarNumber));

        disjunctions.add(new Disjunction(-additionalVar, -node));
        disjunctions.add(new Disjunction(additionalVar, node));

        return additionalVar;
    }

    @Override
    public Integer visitConjunction(CNFParser.ConjunctionContext ctx) {
        Integer left = visitChildren(ctx.cnf(0));
        Integer right = visitChildren(ctx.cnf(1));
        Integer additionalVar = pool.idByName("@" + (++currentNewVarNumber));

        disjunctions.add(new Disjunction(additionalVar, -left, -right));
        disjunctions.add(new Disjunction(left, -additionalVar));
        disjunctions.add(new Disjunction(right, -additionalVar));

        return additionalVar;
    }

    @Override
    public Integer visitDisjunction(CNFParser.DisjunctionContext ctx) {
        Integer left = visitChildren(ctx.cnf(0));
        Integer right = visitChildren(ctx.cnf(1));
        Integer additionalVar = pool.idByName("@" + (++currentNewVarNumber));

        disjunctions.add(new Disjunction(-additionalVar, left, right));
        disjunctions.add(new Disjunction(-left, additionalVar));
        disjunctions.add(new Disjunction(-right, additionalVar));

        return additionalVar;
    }

    @Override
    public Integer visitImplication(CNFParser.ImplicationContext ctx) {
        Integer left = visitChildren(ctx.cnf(0));
        Integer right = visitChildren(ctx.cnf(1));
        Integer additionalVar = pool.idByName("@" + (++currentNewVarNumber));

        disjunctions.add(new Disjunction(-additionalVar, -left, right));
        disjunctions.add(new Disjunction(right, additionalVar));
        disjunctions.add(new Disjunction(-left, additionalVar));

        return additionalVar;
    }
}
