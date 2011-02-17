JNLP Applet Loader provides a way to create a LWJGL Applet Loader based on a JNLP file

It is a maven project and it is based on two modules: 

	jnlpappletloader-applet

		This module is where the main Applet is, it has JNLP utilities in order to let the Applet parse a JNLP file in an easy way.

	jnlpappletloader-deployable

		This module provides a deployable project to deploy on a server in order to check the first module works right.

In jnlpappletloader-status.html you can see how the applet should be configured.

