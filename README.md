# Visual Inspection of CSS and JavaScript

@author kazurayam
@date 26 Jan 2022

## Problem to solve

My previous project ["Visual Inspection in Katalon Studio — Reborn"](https://forum.katalon.com/t/visual-inspection-in-katalon-studio-reborn/57440) enabled to me to compare pages of a web site. It produces diff information of screenshots and HTML sources. The HTML diff info helps me understanding the causes of the visual differences in the screenshot PNG images between 2 environment --- the Production env and the Development env.

Half year has passed since I released the "Visual Inspection" project, I realized that HTML is not enough. Any web app consists of mainly 3 types of web resources: HTML, CSS, and JavaScript (JS). I want to see **the source of CSS and JavaScripts** that are referred by the web pages. And I want to see the diff information of CSS and JS as well.

Let me tell you why I want it. Any web application depend on some **external software products**. For example, my application depends on [jQuery](https://jquery.com/) and [Bootstrap](https://getbootstrap.com/). Once your web application was released, any updates of the external dependencies will put continuous pressure on you developers. You have to upgrade them sometime in near future. However it is a scary task. Why? --- You are scared that some changes in the external dependencies may affect your web pages but you can not predict what will happen. Therefore all you can do is *to look at as many pages of your web app as possible looking for anything unusual*. I call this tiresome and boring activity as **"Visual Inspection"**. My "Visual Inspection" approach can automate it.

However, jQuery and Bootstrap --- these are the collections of CSS and JS files. When I encounter something to investigate, I would need to look at the CSS & JS sources. So **I want to see the diff info of CSS and JS of jQuery & Bootstrap before/after the upgrade. Is it possible?**

## Solution

This project "Visual Inspection of CSS and JS" contains a demonstration [Test Cases/MyAdmin/MyAdmin\_visual\_inspection\_twins](Scripts/MyAdmin/MyAdmin_visual_inspection_twins/Script1643034427943.groovy) that compares 2 URLs:

-   <http://myadmin.kazurayam.com>

-   <http://devadmin.kazurayam.com>

It scrapes all the sources of HTML, CSS, JavaScript refered by the pages. The Test Case uses the [Chrome DevTools Protocol, Network Domain](https://chromedevtools.github.io/devtools-protocol/tot/Network/) in order to recognize the URLs of resources refered by the page. The Test Case saves the files, associate files as pairs, make diff, and compile a report in HTML. A sample output is here:

-   [Demo report](https://kazurayam.github.io/VisualInspectionOfCssAndJs/demo/MyAdmin_visual_inspection_twins-index.html)

This report includes the diff info of CSS and JS files distributed by jQuery and Bootstrap. For example, see the following link. Please note that this link shows the difference of 2 versions of a single product: v1.5.0 and v1.7.2 of the [bootstrap-icons](https://icons.getbootstrap.com/).

-   [diff of bootstrap-icons CSS, v1.5.0 vs v1.7.2](https://kazurayam.github.io/VisualInspectionOfCssAndJs/demo/MyAdmin_visual_inspection_twins/20220126_220156/objects/4c2502854bbc5defa960ad2604c46b46c709eb40.html)

![diff of bootstrap-icons CSS](https://kazurayam.github.io/VisualInspectionOfCssAndJs/images/diff_bootstrap-icons.png)

Please imagine that now I am going to upgrade my web app to use the bootstrap-icons from v1.5.0 to v1.7.2. In case when I find any visual differences between the old and the new, this diff info would help.

## Description

### How to run the demo

You just want open and run [Test Cases/MyAdmin/MyAdmin\_visual\_inspection\_twins](Scripts/MyAdmin/MyAdmin_visual_inspection_twins/Script1643034427943.groovy). The script takes approximately 30 seconds to finish. It will make a report in the `<projectDir>/store` directory.

[Chrome DevTools Protocol](https://chromedevtools.github.io/devtools-protocol/) is a new technology. Katalon Studio v8.2.0 does not support it out of box. I needed to setup the project with some external jar files, which are bundled in the `Drivers` directory in the repository.

You have 2 options.

#### Option1: Using kklisura’s CDP library

-   Katalon Studio: any version, e.g, v8.2.0

-   will use [GitHub kklisura
    /
    chrome-devtools-java-client](https://github.com/kklisura/chrome-devtools-java-client)

-   set the `GlobalVarible.visitSite_by_Selenium4_CDT` in the Execution Profile `default` with value of **false**.

#### Option2: Using Selenium 4

-   Katalon Studio: [v8.2.1 alpha](https://forum.katalon.com/t/studio-8-2-1-alpha-is-now-available-with-selenium-4/61011/) is required

-   will use [selenium-devtools-v96](https://mvnrepository.com/artifact/org.seleniumhq.selenium/selenium-devtools-v96)

-   set the `GlobalVarible.visitSite_by_Selenium4_CDT` in the Execution Profile `default` with value of **true**.

### How the test script is coded

See the source codes of test cases:

-   [Test Cases/MyAdmin/MyAdmin\_visual\_inspection\_twins](./Scripts/MyAdmin/MyAdmin_visual_inspection_twins/Script1643034427943.groovy)

-   [Test Cases/MyAdmin/visitSite](./Scripts/MyAdmin/visitSite/Script1643072442615.groovy)

-   [Test Cases/MyAdmin/materializeCssJs\_by\_kklisure\_CDT](./Scripts/MyAdmin/materializeCssJs_by_kklisura_CDT/Script1643072742739.groovy)

-   [Test Cases/MyAdmin/materializeCssJs\_by\_Selenium4\_CDT](./Scripts/MyAdmin/materializeCssJs_by_Selenium4_CDT/Script1643072750293.groovy)

I learned the following artiles to learn how to write programs that drive Chrome DevTools Protocol.

-   [kklisura’s sample code](https://github.com/kklisura/chrome-devtools-java-client/blob/master/cdt-examples/src/main/java/com/github/kklisura/cdt/examples/InterceptAndBlockUrlsExample.java)

-   [Selenium 4 Key Feature: Network Interception, RAHUL SHETTY](https://rahulshettyacademy.com/blog/index.php/2021/11/04/selenium-4-key-feature-network-interception/)

### How the test script works

1.  I start the [Test Cases/MyAdmin/MyAdmin\_visual\_inspection\_twins](./Scripts/MyAdmin/MyAdmin_visual_inspection_twins/Script1643034427943.groovy)

2.  the script visits 2 web sites

    -   <http://myadmin.kazurayam.com>

    -   <http://devadmin.kazurayam.com>

3.  for each sites, the script does the following

    -   open Chrome browser

    -   let the browser to visit the site URL

    -   browser request HTML, CSS, JS, images etc to the site

    -   the site responds to the requests from the browser

    -   browser notifies the script of the `responseReceveied` events which includes HTTP Status (200 OK), the URL of the resource, the MIME-Type

    -   when the event stream ceased, the script send HTTP GET requests to the original site for the source files of CSS and JS

    -   the script saves the resource files

4.  the script process the files and compile a report

![Sequence diagram](https://kazurayam.github.io/VisualInspectionOfCssAndJs/diagrams/out/sequence/sequence.png)
