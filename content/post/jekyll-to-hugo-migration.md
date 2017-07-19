---
date: 2017-07-19T05:30:54+02:00
title: Jekyll To Hugo Migration Notes
tags: [blog, hugo, jekyll, ruby]
---

This blog is statically generated page hosted on my GitHub. Till recently the job of generating static files was handled by Jekyll. I didn't do a lot of research before I first used it. I don't think it would really help in my case. I used Jekyll because GitHub has support for it and it was convenient. I could store only the sources and GitHub will automatically compile the page when I push some changes. Jekyll was doing fine job. So the question arises...

## Why change what works?

It's just painful to work with. I had a lot of problems with Ruby dependencies in wrong versions and it drove me crazy. Please don't get me wrong, I don't want to say that Ruby sucks or that Jekyll does. I really don't care. The baseline here is that I had problems with this and I don't want do deal with them anymore.

The death of my system SSD was really the trigger. New installation of Linux and all the tools was required. A flash of memories from previously installing Jekyll, and I decided to migrate to something else.

## What should the new generator provide?

The problem I faced was that my page was already online for a while. I strongly believe that you should never break page URLs. Once the URL is available on the internet it should point to it's content forever. I also didn't want to rewrite all my content to new format so it should handle markdown formatted content with YAML metadata. So the absolute "must have" was to be able to publish my old content with old URLs and without many changes.

After some googling for static site generators I've decided to try [Hugo](https://gohugo.io/).

## So what are the steps to migrate?

### The theme

Jekyll has great support for [Sass](http://sass-lang.com/) which is a language that compiles to CSS. Hugo doesn't support this at all, and there is little chance it ever will. It turns out that Sass compiler is written in Ruby. I didn't see that coming... I've decided that for now I'll just use the compiled CSS version, and deal with Sass later. A little research on this showed that there is [libsaas](http://sass-lang.org/libsass) that can be used in any language. Other than that, there were no surprises and porting current theme went smoothly.

### The content

All pages and posts required addition of [aliases](https://gohugo.io/extras/aliases/) to get old URLs to work. In posts containing code (so almost all of them) I needed to change the way syntax highlighter worked. Hugo uses the same syntax highlighter as Jekyll, which is [Pygments](http://pygments.org/). Unfortunately in Jekyll marking section of code uses special syntax. In Hugo I was able to configure it to use popular three backtick formatting. Just like on [GitHub](https://help.github.com/articles/creating-and-highlighting-code-blocks/). Of course then I had to modify all the posts with the new markups.


### Disqus comments and Google Analytics

Last step was to think about how Disqus comments and GA will behave with my changes. Disqus is linking comments to given URL so changing target URLs (which are different for Hugo) will cause the old comments do disappear under posts. Luckily it provides the tool for comment migration. It's just simple CSV with two columns - old page URL, and new page URL. Works like a charm!

I thought about Google Analytics for a while and I don't see anything that I should do. I don't do much analyzing. After a short while it'll simply show statistics for new URLs. Do you know if I should do anything with this?

### Deployment

Hugo has great [instructions on deploying with GitHub Page](https://gohugo.io/tutorials/github-pages-blog/). Last piece was to create a small publication script that will compile the page to the *gh-pages* branch and push it to the origin:

```bash
#!/bin/bash
hugo
cd public && git add --all && git commit -m "Publishing to gh-pages" && cd ..
git push origin gh-pages

```

## Enjoy!

That is all I needed to do in order to write this post :smile:. I hope we'll get along with Hugo pretty well. Thank you for reading and see you next time!
