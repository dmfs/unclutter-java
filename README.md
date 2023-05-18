# Unclutter Java

Reduce cognitive load when parsing Java code by folding some verbose syntax.

## Functional Interfaces

Reduces usages of functional interfaces (interfaces annotated with <code>@FunctionalInterface</code>) to what you would
expect when you come
from a language like Python.

### Examples

```java
Function<Baz,Buzz> f=foo::bar;
f.apply(baz); // unfolded
f(baz);       // folded
        
Predicate<String> isBlank=String::isBlank;
if(isBlank.test("not blank")) { … } // unfolded
if(isBlank("not blank")) { … }      // folded
```

Note, only the sole non-`default` and non-`static` method of a functional interface is folded.

## Creating instances

Removes visual noise from constructor calls.

### Examples

```java
new Foo(bar, baz); // unfolded
Foo(bar, baz);     // folded

new Foo<>(bar, baz); // unfolded
Foo⋄(bar, baz);      // folded

new com.example.Foo<String>(bar, baz); // unfolded
…Foo⋄(bar, baz);                       // folded
```

## express-json

Folds (simple) <a href="https://github.com/dmfs/express">express-json</a> compositions into something that
looks like JSON.

### Examples

```java

new Object(
    new Member("a", "b"),
    new Member("c", new Array(1, 2, 3))
); // unfolded

{
    "a": "b",
    "c": [1, 2, 3]
}; // folded
```

## Logging

Folds log statements and consumers into a "comment". This reduces distraction and helps to focus on the actual
functionality.

### Examples

```java
log.trace("some trace log"); // unfolded
/*log*/ // folded
        

rxflowable
    .map(mapFunction)
    .doOnNext(value -> log.trace("next value was {}", value)) // unfolded
    .subscribe(subscriber);

rxflowable
    .map(mapFunction)
    .doOnNext(/*log*/) // folded
    .subscribe(subscriber);
```

## Comparable.compareTo

Readable calls to compareTo

### Examples

```java
foo.compareTo(baz) < 0 // unfolded
foo < baz              // folded
```

## License

Copyright 2023 dmfs GmbH

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
License. You may obtain a copy of the
License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "
AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and
limitations under the License.

