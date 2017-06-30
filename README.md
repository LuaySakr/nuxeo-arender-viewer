# About / Synopsis

* Use aRender PDF previewer instead of default PDF.js

# Table of contents

# Installation

## aRender

- Install an [aRender HMI](http://www.arender.fr/) server with CMIS support
- Open `/war/WEB-INF/classes/arender.xml`, locate `cmisConnection` bean to add the following:
 ```xml
    <property name="nuxeoPortalSecret" value="aRenderTest" />
    <property name="annotationsPath">
        <value>/default-domain/workspaces/annotations</value>
    </property>
    <property name="annotationFolderName">
        <value>SuperAnnotations</value>
    </property>
 ```

## Nuxeo

- Create a `Folder` or a `Workspace` into `/default-domain/workspaces/annotations`.
- Annotations Document will be stored inside with the logged user. On order to let users view and create annotations, you should add a `ReadWrite` permission to the right group. 
- In order to change the default password (`aRenderTest`), add this kind of contribution:
 ```xml
 <require>org.nuxeo.viewer.arender.auth</require>
 
 <extension target="org.nuxeo.ecm.platform.ui.web.auth.service.PluggableAuthenticationService" point="authenticators">
   <authenticationPlugin name="ARENDER_PORTAL_AUTH">
     <parameters>
       <parameter name="secret">myNewSecret</parameter>
     </parameters>
   </authenticationPlugin>
 </extension>
 ```

### Configuration Key

- `arender.uri`: aRender HMI server URI. Default: `http://localhost:8081/ARenderHMI`.
- `arender.nuxeo.cmis`: Force an URI to aRender to request on Nuxeo. It can be useful when aRender daemon do not access Nuxeo with the same network than the user's browser. Default: **Computed from request**. Example: `http://10.213.3.37:8080/nuxeo/atom/cmis`.

# Usage

## Features

- Use aRender instead of PDF.js previewer on PDF files and only on the **main blob**.

# Code
## Limitations

- aRender requires to increase Segment Maximum Size as it uses original UUID document as annotations path segment. Size is increased to `36`.
- CMIS do not support multiple stream on one node; and cannot access any other files than the main. Thus, aRender preview is disabled when not on main content.
- Annotations are stored as Document, and created with the logged User. There is no way to have user's dedicated annotation, and annotations container must be accessible with `ReadWrite` access to everyone.

## Build

```bash
mvn clean install
```

## Deploy

```bash
nuxeoctl mp-install arender-viewer-package/target/arender-viewer-package-*zip
```

# License

[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

# About Nuxeo

The [Nuxeo Platform](http://www.nuxeo.com/products/content-management-platform/) is an open source customizable and extensible content management platform for building business applications. It provides the foundation for developing [document management](http://www.nuxeo.com/solutions/document-management/), [digital asset management](http://www.nuxeo.com/solutions/digital-asset-management/), [case management application](http://www.nuxeo.com/solutions/case-management/) and [knowledge management](http://www.nuxeo.com/solutions/advanced-knowledge-base/). You can easily add features using ready-to-use addons or by extending the platform using its extension point system.

The Nuxeo Platform is developed and supported by Nuxeo, with contributions from the community.

Nuxeo dramatically improves how content-based applications are built, managed and deployed, making customers more agile, innovative and successful. Nuxeo provides a next generation, enterprise ready platform for building traditional and cutting-edge content oriented applications. Combining a powerful application development environment with
SaaS-based tools and a modular architecture, the Nuxeo Platform and Products provide clear business value to some of the most recognizable brands including Verizon, Electronic Arts, Sharp, FICO, the U.S. Navy, and Boeing. Nuxeo is headquartered in New York and Paris.
More information is available at [www.nuxeo.com](http://www.nuxeo.com).