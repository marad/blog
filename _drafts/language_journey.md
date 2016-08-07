---
title: "Language Journey"
tags: ["clojure", "programming", "data"]
categories: programming clojure
---

I've been quiet recently. It's partially due to having a new member of my family (and thus less time for... well... everything) but mostly I've been exploring different programming languages and different styles they come with.

If you read this blog you surely know that I am a Scala enthusiast. I love Scala because it's a very expressive language, especially compared to Java. Unfortunately it has also a dark side. It's very complicated. I've realized this when I was using it at work with my colleagues that didn't have much experience with Scala. When there are people with varying knowledge of Scala working on the same project there is a lot of confusion going on :)

So as much as I love Scala it's very hard to master. This set me off on a journey to learn something new. I've always been interested in learning new languages and concepts. One example of this is my [hyper-router][hyper-router] project. It's a routing library for popular HTTP server written in [Rust][rust]. I've created the library as Rust training but it seems to be [hyper-query][quite popular] lightweight library for routing.

Rust is unique systems programming language. It's uniqueness comes from memory management model. It doesn't have garbage collector and it doesn't require programmer to free memory himself. Impossible? Not at all!

The language introduces the concept of [ownership][ownership] and [borrowing][borrowing]. These are checked by the compiler. Yes, the compiler checks if you manage your memory properly at compilation time. This of course is not free. Programmer must comply with some rules or he will get compile errors which are actually very helpful.

I've also tried [elixir][elixir] which is also very different language. It runs on Erlang virtual machine which is very interesting itself. The VM is an environment for running _Actors_, and features hot re-loading code. This in fact allows the Erlang programs to run with no downtime. Another amazing thing is that you can create a cluster at VM level. This basically means that your program doesn't know if it runs on one or hundred hosts. This is all thanks to the [actor model][actor-model] which scales very well.

The thing I built with elixir is [Eljure][eljure]. Clojure-like programming language interpreter. I don't consider it ready for anything, but I've sure learned a lot about Erlang, Elixir and about LISPy languages. This was very fun and rewarding project to work on!

# Clojure

And this all takes me to the language I'm exploring lately: _[Clojure][clojure]_. I must say that I had about three attempts to dive in. At first I was having issues with the number of parens. Then I got quite annoyed by Clojure's most popular build tool Leiningen. It felt heavy and hard to configure properly. But there was something about the language... I wanted to be a Clojure ninja but just couldn't get a hang of it.

Then something just clicked into place and suddenly Clojure ecosystem wasn't that much of an issue. I really don't know the exact time this happened. Or maybe it wasn't just a _click_. I spent many hours playing with Clojure build tools, watching tutorials and trying to create something myself.

Now I can just say I think it was totally worth it! Rich Hickey, creator of Clojure, has made an excellent language for the JVM platform. After watching many of his talks about programming in general I now understand why is that. He is a great thinker and the language itself is thought trough. With Clojure he tries to solve the biggest problem of any system, which is complexity. And the results are astonishing!

So how exactly is Clojure different from other programming languages? Here are some of its features that sets it apart in no particular order:

### Simple

I'm not kidding! I know that I said I've given Clojure three chances but it was never because of complexity of the language. Here are some of the things that are simpler than in other languages:

- operator precedence does not exist here, you basically tell and see the order with all these parens
- there are just a few [special forms][special-forms]
- you don't have to know anything about multiple inheritance problem
- uniform and clear syntax
- no boilerplate (not counting the parens [there are too many to count them...])
- lets you interact with running instance of your program via REPL (see below)

### Persistent Data Structures

Your data is immutable. If you want to change some small detail about it, you have to copy it in the new place with changed just one little detail, or do you? 

[Persistent Data Structures][pds] come to the rescue! Basic idea here is that if your data is immutable then you can reuse the parts that has not changed and allocate memory only for the new updated value. For example given an immutable linked list, if you want to add one element at the beginning of the list you simply create one node for that element with _next_ pointing at the head of the old list.

Why is that important, you ask? It's because of multi-threaded environments. When you run your code in parallel and the data is immutable there are no synchronization issues. The data cannot change so there is nothing to synchronize! For rare moments that you really have to modify data from multiple threads Clojure has you covered with Software Transactional Memory which is briefly described below.

### Functional Language

This one is an new old hotness! Functional languages are around for a while (LISP from 1958!), but they seem to have gained on popularity lately. Clojure is functional. You are mostly spending time just passing some data along with functions to other functions to create some more data or functions :)

Functions are great because they are very composable. At first you can build just simple stuff and as you are progressing with your work you build upon that to create more sophisticated stuff out of that simple stuff. That is the idea behind this whole functional programming.

Your program is probably processing some data like any other program do. You get something, process it and return something new. In that sense your program is like a function - just built out of smaller functions.

Understanding this, pushed me to turn my back on OOP. I've been raised on OOP. This was one hard breakup. I'm just tired of specifying what a brick can do to itself, creating a mutable wibily-wobly construct that will most likely crash your program, because it somehow ended up in some illegal state.

A function is simple and thus easy to understand. Composing two functions is also simple. Now repeat that till you have a working software.

### Software Transactional Memory

Persistent data structures are good and all but sometimes you just have to modify some state. This is life. This is where the [software transactional memory][stm] comes at the stage. It ensures that you don't push yourself over the edge with threading and keeps your data mutations safe. You don't have to think about it much as it's integrated into [refs][refs], [agents][agents] and [atoms][atoms] in Clojure, but it's there and it watches your back!

### Focused on Data and Data Processing

Rich Hickey designed Clojure so that the code is about the problem you are solving, not the required boilerplate code to get basic stuff going. Additionally it's packed with tons and tons of functions that mostly work for maps as well as vectors and lists. They just work as you would expect them to. One example can be _map_ function, which takes a function and a collection and applies the function to each element of the collection creating new collection from result values.

{% highlight clojure %}
=> (map inc [1 2 3 4])
[2 3 4 5]
{% endhighlight %}

The _inc_ is a function that increments a value by 1. Above we just say that we want to increment every element in given vector and the new vector is returned containing incremented values.

How should this work for maps? Should we map the function over the keys or the values of the map? A map is really just a set of entries that have a key and a value so:

{% highlight clojure %}
=> (map identity {:a 1, :b 2})
([:a 1] [:b 2])
{% endhighlight %}

_identity_ function just returns it's argument. As we can clearly see map just applies the function to a vector. First element is the key, second element is the value. This just makes sense (I'm looking at you Python!).

There is more to it! Vast amount of functions operate on all available data structures (lists, vectors, sets and maps). This means that you don't have to remember special variants of _map_ function for each of the data types which is good for your brain :)

### Awesome `nil` Support

In Java you should fear the _null_ hiding literally everywhere. In Clojure _nil_ is everywhere but all the core functions handle it in a sane way and you will most likely never get any _NullPointerException_ from Clojure code. If you did, you probably used some Java code. See this:

{% highlight clojure %}
(update nil nil println)
{% endhighlight %}

Here _update_ is a function that takes some associative structure (like a map or a vector) and updates it's value at given key/index by using function. In the example above first _nil_ is the collection I want to update. Second _nil_ is the index I want to update and _println_ is a function that displays what it's given to it and returns _nil_. The update function will return a _map_ with key _nil_ set to _nil_:

{% highlight clojure %}
{nil nil}
{% endhighlight %}

If you think about it, you've got exactly what you ordered. After the _update_ function you wanted to have key _nil_ (which you passed as an argument) associated with result of calling a function _println_ on the previous value of key _nil_. The previous value was also _nil_ so you additionally have it printed on the standard output. The _println_ function also returns a _nil_ and that value is placed as your new value for key _nil_. That is a lot of _nils_ and not even one _NullPointerException_.

### The Holy REPL

REPL stands for _read eval print loop_. This is the place where all Clojure programmers spend their day having fun. It's basically a prompt in which you can enter some Clojure code to evaluate it and print the result. Why it is so great? Because you can play with running instance of your program. In fact there is rarely a need to restart the program. If you've changed your code you just load it into a running instance and that's it.

This interactive mode is great for testing out ideas or debugging. Once you go REPL you never want to go back to the recompile/test horror.

### Runs on JVM side by side with Java

Alright, this is not the feature that sets Clojure apart from other languages, as many others do run on JVM. However, this is a very nice feature to have because Java world is filled with libraries to do all sorts of things. You can use all ecosystem that you already know (if you already know Java ;)).

This is a great thing to have, especially that you can add Clojure to your existing Java project by adding just one dependency. It's that simple!

## Want to start using Clojure?

The best way is to start coding in Clojure without its ecosystem. TODO: 4clojure, contests...

[rust]: https://www.rust-lang.org/en-US/
[hyper-router]: https://github.com/marad/hyper-router
[hyper-query]: https://crates.io/search?q=hyper
[ownership]: https://doc.rust-lang.org/book/ownership.html
[borrowing]: https://doc.rust-lang.org/book/references-and-borrowing.html
[elixir]: http://elixir-lang.org/
[actor-model]: https://en.wikipedia.org/wiki/Actor_model
[eljure]: https://github.com/marad/eljure
[clojure]: https://clojure.org/
[stm]: https://en.wikipedia.org/wiki/Software_transactional_memory
[refs]: http://clojure.org/reference/refs
[agents]: http://clojure.org/reference/agents
[atoms]: http://clojure.org/reference/atoms
[special-forms]: http://clojure.org/reference/special_forms

