package org.kefirsf.bb.proc;

import java.io.IOException;
import java.util.List;

/**
 * The bbcode class
 *
 * @author Kefir
 */
public class ProcCode implements Comparable<ProcCode> {
    /**
     * template for build result char sequence
     */
    private final ProcTemplate template;
    /**
     * Pattern for parsing code
     */
    private final List<ProcPattern> patterns;
    /**
     * Priority. If priority higher then code be checking early.
     */
    private final int priority;
    /**
     * The code name.
     */
    private final String name;

    /**
     * Create the bb-code with priority
     *
     * @param patterns  pattern to parse the source text
     * @param template template to build target text
     * @param name     name of code
     * @param priority priority. If priority higher then code be checking early.
     */
    public ProcCode(List<ProcPattern> patterns, ProcTemplate template, String name, int priority) {
        this.template = template;
        this.priority = priority;
        this.name = name;
        this.patterns = patterns;
    }

    /**
     * Parse bb-code
     * <p/>
     * Before invocation suspicious method must be call
     *
     * @param context the bb-processing context
     * @return true - if parse source
     *         false - if can't parse code
     * @throws IOException if can't append to target
     * @throws NestingException if nesting is too big.
     */
    public boolean process(Context context) throws IOException, NestingException {
        for(ProcPattern pattern: patterns){
            Context codeContext = new Context(context);
            if (pattern.parse(codeContext)) {
                codeContext.mergeWithParent();
                template.generate(context);
                return true;
            }
        }

        return false;
    }

    /**
     * Check if next sequence can be parsed with this code.
     * It's most called method in this project.
     *
     * @param source text source
     * @return true - if next sequence can be parsed with this code;
     *         false - only if next sequence can't be parsed with this code.
     */
    public boolean suspicious(Source source) {
        for(ProcPattern pattern:patterns){
            if(pattern.suspicious(source)){
                return true;
            }
        }
        return false;
    }

    /**
     * Compare by priorities
     */
    public int compareTo(ProcCode code) {
        return this.priority - code.priority;
    }

    /**
     * Get code name
     *
     * @return code name
     */
    public String getName() {
        return name;
    }

    public boolean startsWithConstant(){
        for(ProcPattern pattern: patterns){
            if(!pattern.startsWithConstant()){
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProcCode procCode = (ProcCode) o;

        if (name != null ? !name.equals(procCode.name) : procCode.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
