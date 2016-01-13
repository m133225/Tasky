package parser;

import parser.Parser.FieldType;

class KeywordMarker implements Comparable<KeywordMarker> {
    int index;
    FieldType typeOfField;

    int getIndex() {
        return index;
    }

    void setIndex(int i) {
        index = i;
    }

    FieldType getFieldType() {
        return typeOfField;
    }

    void setFieldType(FieldType fieldType) {
        typeOfField = fieldType;
    }

    @Override
    public int compareTo(KeywordMarker o) {
        if (this.index < o.getIndex()) {
            return -1;
        } else if (this.index > o.getIndex()) {
            return 1;
        } else {
            return 0;
        }
    }
}