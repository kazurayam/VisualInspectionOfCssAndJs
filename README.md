# Visual Inspection of CSS and JavaScript

@author kazurayam
@date 26 Jan 2022

## Problem to solve

My previous project [Visual Inspection in Katalon Studio — Reborn](https://forum.katalon.com/t/visual-inspection-in-katalon-studio-reborn/57440) enabled to me to compare screenshots and HTML source files of web pages. It is really nice that I can see the diff information of HTML source files. It helps me to understand the reasons why the visual differences are found in the screenshots between the Production environment and the Development environment.

Half year passed after I released the "Visual Inspection" project, I realized that HTML is not enough. Any Web app consists of mainly 3 types of web resources: HTML, CSS, and JavaScript (JS). I want to see **the source of CSS and JavaScripts** that are referred by the web pages of my Application Under Test. I want to see the diff information of CSS and JS.

Let me tell you another story. Any web application depend on some external software products. For example, my application depends on [jQuery](https://jquery.com/) and [Bootstrap](https://getbootstrap.com/). Once your web application is released for the production use, new releases of your dependent products will give you continuous pressure. You have to upgrade the products sometime in near future. It is a very scary task to upgrade the products. What are you fear for? You are scared that some changes of the external products may affects your web pages, and you can not predict what will happen. Therefore all you can do is to "look at as many pages of your web app as possible to find something unusual".

My "Visual Inspection" project would help you carry out this fearful task. However, jQuery and Bootstrap --- these are really a collection of CSS and JS. When I find something to investigate, I will need to look at the CSS & JS sources.

**I want to see the diff info of CSS and JS of before/after the upgrade. Is it possible?**

## Solution

I have updated the [materialstore](https://github.com/kazurayam/materialstore) library to v0.1.10, which works behind the Visual Inspection project. Now it enables me to make diff information of CSS and JS.

## Description

See the folloing report that were created by this "Visual Inspection of CSS and JS" project.

-   [Demo report](https://kazurayam.github.io/VisualInspectionOfCssAndJs/store/MyAdmin_visual_inspection_twins-index.html)
