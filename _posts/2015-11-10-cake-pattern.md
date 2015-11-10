---
title: The Cake Pattern
date: 2015-11-10 12:17:17
tags: ["cake pattern", "dependency injection", "dependency management", "dependencies"]
categories: programming scala
---

Let's think a bit about how you should structure your program. If your codebase has less than 500 lines of code it is not really that hard to manage. You can simply manually create the objects and their dependencies to construct your application. The problem emerges when your codebase grows.  Managing houndreds of dependencies manually is a tedious task. We need some kind of mechanism to do that for us. If you know Java you probably have heard about Dependency Injection (DI) and Inversion of Control (IoC). This is one way to approach the problem. There is another one and it's called _Cake Pattern_.

# Dependency Injection

I believe we should start with a paragraph or two highlighting some aspects of dependency injection. Before I learned about _Cake Pattern_ I thought that it was some kind of dependency injection. I was so wrong. In fact - this actually caused me some trouble understanding the _Cake Pattern_.

Dependency Injection uses inversion of control to do it's trick. This means that you only say what are your components and each component knows what other components it depends on. Later some container instantiates your components and their dependencies for you. You never have to write a single `new`. This makes developing much easier. You can focus on what is it you want to do instead of how to wire everything up.

# The Cake Pattern

_Cake Pattern_ is solves the same problem. It allows you to specify dependencies of your components, but it does this a little different. It never takes the control of the component creation. How is that possible? Well - by proper structuring your code. That's the trick. Did you think that the _pattern_ part was a hoax?

Let's see some example. Assume that we want to create the `Bakery` which bakes bread (duh!). There is also the `Mill` which can supply some flour. Obviously the `Bakery` is going to need the flour from the `Mill` - thus creating dependency. Also let's not dive to deep in this and assume that `Mill` just produces flour out of thin air.

With dependency injection you would normally create the two components, and somehow tell the injector that `Bakery` would like to have `Mill` instance injected. The cake pattern is different because it uses interfaces to denote that the dependency will be available some time later. To do that we use a few scala `trait`s that enable us to mix the implementation of that interfaces when creating the actual working instance of an object.

We can start by defining the component and interface for the `Mill`:

{% highlight scala %}
case class Flour()

trait MillComponent {
    def mill: Mill

    trait Mill {
        def produceFlour(): Flour
    }
}
{% endhighlight %}

We've created the `Flour` class to represent the goods produced by the `Mill`. Then you can see that we've declared `MillComponent` which promises to us that it's implementations will return the `Mill` instance through `mill` method. Also there is `Mill` trait which is just interface telling us that it can `produceFlour`. This construct is like the definition of our component.

Now let's create the actual implementation for the component and the `Mill` itself:

{% highlight scala %}
trait RegularMillComponent extends MillComponent {
    val mill: Mill = new RegularMill

    class RegularMill extends Mill {
        def produceFlour() = Flour()
    }
}
{% endhighlight %}

This is quite simple. We create the component by extending its _definition_ we wrote above - note that this component is still a `trait`. The `RegularMill` implementation is, on the other hand, a class that simply implements the `Mill` interface.

We're done with this component. Please note that its `mill` field is `val`. This means that we want only one instance of it. We could have left the `def` there and then components would create new mill instance everytime they used the `mill` method.

On to the bakery. To create the component we use the same template as before:

{% highlight scala %}
case class Bread(flour: Flour)

trait BakeryComponent {
    def bakery: Bakery

    trait Bakery {
        def bakeBread(): Bread
    }
}
{% endhighlight %}

The interesting bit is that this does not tell us anything about the dependency. At this level we don't need any `Flour` - we're just declaring that the component provides `Bakery` that can `bakeBread`.

Now let's see how we can declare the dependency and use it to implement `RegularBakery`:

{% highlight scala %}
trait RegularBakeryComponent extends BakeryComponent {
    this: MillComponent =>

    def bakery: Bakery = new RegularBakery

    class RegularBakery extends Bakery {
        def bakeBread(): Bread = Bread(mill.produceFlour())
    }
}
{% endhighlight %}

By specifying `this: MillComponent =>` at the beginning of the trait we are telling the compiler that this trait can be mixed in only with objects that also have `MillComponent` trait mixed in. This allows us to use the `mill` while implementing the `bakeBread()` method.

Soooo... one thing left. How to use this? It's quite simple, just mix everything in one object:

{% highlight scala %}
val app = new Object
          with RegularMillComponent
          with RegularBakeryComponent

val bread = app.bakery.bakeBread()
{% endhighlight %}

Now if you try to create an instance of `RegularBakeryComponent` without some implementation of `MillComponent` then the program will not compile at all. This means that if your program compiles all the dependencies are satisfied.

# Pros & Cons

As you can see in cake pattern there is no _injection_ of dependencies. You simply declare the dependencies and then mix all the components in one _Object_. Manual dependency management is a lot less _magical_ than the standard dependency injection. Sure it might be a little overwhelming with all those `trait`s and mixing, but this is just using standard language features. If you get this once you'll never have any problem with this type of dependency management again. Another great thing is that dependencies are checked at compile time so there are no long stack traces telling you that some dependency is missing.

On the sad part - you might have noticed that it's a little verbose when compared to Spring's `@Component` and `@Autowire`. Also it's a bit less flexible than standard DI. Imagine that you want one of the components to use one implementation of some interface and another one want different implementation of the same interface. In Spring you would use `@Qualifier` for this. Here it's a bit more tricky and I could explore this a bit more in some future post.

# Summary

Classical dependency injection with inversion of control gives us some nice features, as the container at runtime can decide which component it's going to inject. With cake pattern everything is wired up at compile time so this mechanism is a bit more _low level_. You can of course make your own logic for instantiating components - you never gave up control over that - but this means you have a bit more work to do :)

I hope that you've learned something new by reading this post. I definatelly did while writing it :)

You can view whole example code at my [github][example].

[example]: https://github.com/marad/cake-pattern-example
