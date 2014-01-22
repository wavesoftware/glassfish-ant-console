[![Build Status](https://secure.travis-ci.org/wavesoftware/glassfish-ant-console.png)](http://travis-ci.org/wavesoftware/glassfish-ant-console)

Introduction
------------

Project has the goal of adding the ability to integrate with *Glassfish 3 Application Server* server from *ANT* tasks. 
It adds some tasks for `auth-realm`, `resource`, `pool` to standard tasks supplied by *Oracle* in `glassfish-ant-tasks` project.

Tasks
-----

Added tasks:

 * `glassfish-auth-realm` [read help](https://github.com/wavesoftware/glassfish-ant-console/wiki/task-glassfish-auth-realm)

Oracle tasks included (these are not described here):

 `glassfish-embedded-deploy`, `glassfish-embedded-stop`, `glassfish-embedded-start`, `glassfish-embedded-admin`, `glassfish-embedded-undeploy`, `glassfish-admin`, `glassfish-start`, `glassfish-stop`, `glassfish-deploy`, `glassfish-undeploy`, `glassfish-redeploy`, `glassfish-component`, `sun-appserv-admin`, `sun-appserv-start`, `sun-appserv-stop`, `sun-appserv-deploy`, `sun-appserv-undeploy`, `sun-appserv-redeploy`, `sun-appserv-update`, `sun-appserv-component`

Example
-------

Example usage using [maven-ant-tasks](http://maven.apache.org/ant-tasks/index.html):

```xml
<target name="-deploy-auth-realm" depends="init,-mvn-bootstrap">
	<!-- loads lib from maven repo -->
	<artifact:dependencies pathId="libs.glassfish-ant-console.classpath">
		<dependency 
			groupId="com.github.wavesoftware" 
			artifactId="glassfish-ant-console" 
			version="0.1.4" 
			type="jar" />
	</artifact:dependencies>
	<typedef 
		resource="com/github/wavesoftware/glassfish/ant/tasks/antlib.xml"
		classpathref="libs.glassfish-ant-console.classpath" />
	<!-- reads properties from xml -->
	<xmlproperty file="${basedir}/setup/glassfish-console.xml" />
	<!-- action="auto" means that realm will be automatically created/recreated -->
	<glassfish-auth-realm 
		type="jdbc" 
		action="auto" 
		installDir="${j2ee.server.home}">
		<!-- prefix: glassfish-console.security.realm is used automatically -->
		<useProperties prefix="mycustom.realm" />
		<property name="re-create" value="yes" />
	</glassfish-auth-realm>
</target>
<target name="-post-run-deploy" depends="-deploy-auth-realm" />
```

Sample xml with properties:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<glassfish-console xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			 xsi:noNamespaceSchemaLocation="https://raw.github.com/wavesoftware/glassfish-ant-console/master/docs/glassfish-console.xsd">
	<security>
		<realm>
			<name>JDBC_realm</name>
			<class>com.sun.enterprise.security.auth.realm.jdbc.JDBCRealm</class>
			<jaas-context>jdbcRealm</jaas-context>
			<datasource-jndi>jdbc/MyApp</datasource-jndi>
			<user-table>users</user-table>
			<user-name-column>login</user-name-column>
			<password-column>password</password-column>
			<group-table>user_group</group-table>
			<group-name-column>group_name</group-name-column>
			<group-table-user-name-column>user_name</group-table-user-name-column>
			<digest-algorithm>MD5</digest-algorithm>
		</realm>
	</security>
</glassfish-console>
```

Installation
------------

For now only Maven is supported. Use maven-ant-tasks for ease.

```xml
<dependency>
	<groupId>com.github.wavesoftware</groupId>
	<artifactId>glassfish-ant-console</artifactId>
	<version>0.1.4</version>
</dependency>
```
