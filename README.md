OnTrack

Development environment
- Java 1.7 <
- GWT 2.5.1
- Eclipse Kepler(4.3) <
- AJDT Eclipse Plugin
- GWT Eclipse Plugin

- user: admin@ontrack.com
- pass: admin


If you want a manual package generation, please run
mvn package -Pprod -Dmaven.test.skip=true

and if you want back to development configuration, run again
mvn clean package -Dmaven.test.skip=true

In one of it steps, maven get the environment.prod file and copy it to
the configurantion folder. So you keep your production configuration
even if you build and run inside eclipse.

** IMPORTANT **

Log4J doesn't work at shared package
