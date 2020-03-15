package uk.co.bjdavies.api.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author ben.davies99@outlook.com (Ben Davies)
 * @since 1.0.0
 */
public class WhereStatement {

    private final List<WhereStatement> group = new ArrayList<>();
    private String key, value;
    private Comparator comparator;
    private Operator operator;


    public WhereStatement(String key, String value) {
        this(key, value, Comparator.EQUALS);
    }

    public WhereStatement(String key, Object value) {
        this(key, String.valueOf(value));
    }

    public WhereStatement(String value) {
        this.value = value;
    }

    public WhereStatement(Operator operator, WhereStatement... group) {
        Collections.addAll(this.group, group);
        this.operator = operator;
    }


    public WhereStatement(String key, String value, Comparator comparator) {
        this.key = key;
        this.value = value;
        this.comparator = comparator;
    }

    public WhereStatement(String key, Object value, Comparator comparator) {
        this.key = key;
        this.value = String.valueOf(value);
        this.comparator = comparator;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Comparator getComparator() {
        return comparator;
    }

    public void setComparator(Comparator comparator) {
        this.comparator = comparator;
    }

    public List<WhereStatement> getGroup() {
        return group;
    }

    public WhereStatement add(WhereStatement statement) {
        this.group.add(statement);
        return this;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }
}
