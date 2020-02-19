package com.github.ftfetter.pocnonblockingcache;

import java.time.LocalDate;

public class Foo {

    private String name;
    private LocalDate date;
    private Long value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }


    public static final class FooBuilder {
        private String name;
        private LocalDate date;
        private Long value;

        private FooBuilder() {
        }

        public static FooBuilder aFoo() {
            return new FooBuilder();
        }

        public FooBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public FooBuilder withDate(LocalDate date) {
            this.date = date;
            return this;
        }

        public FooBuilder withValue(Long value) {
            this.value = value;
            return this;
        }

        public Foo build() {
            Foo foo = new Foo();
            foo.setName(name);
            foo.setDate(date);
            foo.setValue(value);
            return foo;
        }
    }
}
