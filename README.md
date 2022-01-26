# Visual Inspection of CSS and JavaScript

@author kazurayam
@date 26 Jan 2022

## Problem to solve

Any Web app consists of mainly 3 types of web resources: HTML, CSS, and JavaScript (JS). My previous project [Visual Inspection in Katalon Studio — Reborn](https://forum.katalon.com/t/visual-inspection-in-katalon-studio-reborn/57440) enabled to me to compare screenshots and HTML source files of web pages. It is really nice that I can see the diff information of HTML source files. It helps me to understand the reasons why the visual differences are found in the screenshots between the Production environment and the Development environment.

Half year passed after I released the "Visual Inspection" project, I realized that HTML is not enough. I want to see **the source of CSS and JavaScripts** that are referred by the web pages of my Application Under Test. I want to see the diff information of CSS and JS.

Let me tell you another story. Any web application depend on some external software products. For example, my application depends on [jQuery](https://jquery.com/) and [Bootstrap](https://getbootstrap.com/). Once your web application is released for the productional use, new releases of the external products will give you continuous pressure. You have to upgrade the products sometime in near future. It is a very scary task to upgrade the external software products that your web app depends on. Nobody likes to do it, but you have to sooner or later. My "Visual Inspection" project would help you carry out this fearful task. However, jQuery and Bootstrap --- these are really a collection of Cascading Stylesheets and scripts written in JavaScript. When I find something to investigate, I would need to read the CSS & JS sources.

**How can I get the diff info of CSS and JS of before/after the upgrade?**

## Solution

I have updated the [materialstore](https://github.com/kazurayam/materialstore) library to v0.1.10, which works behind the Visual Inspection project. Now it enables me to make diff information of CSS and JS.

## Description
