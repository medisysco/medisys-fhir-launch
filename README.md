# [medisys-fhir-launch][103]

All of the projects of  [Medical Systems Co.][100]  are mavenized, It's a maven  `POM` project. This is the launch of [Medical Systems Co.][100]'s `FHIR (Fast Healthcare Interoperability Resources)` projects. `FHIR` related projects will be the ultimate child of the [medisys-fhir-launch][103]. It will be package as `POM`. The immediate child projects of the [medisys-fhir-launch][103] are able to access all of the properties, dependencies as well as profiles by following way:


```xml
<launch>
    <artifactId>medisys-fhir-launch</artifactId>
    <groupId>my.com.medisys.fhir</groupId>
    <version>1.0.00-SNAPSHOT</version>
</launch>
```
All of the resource and library of [Medical Systems Co.][100] permitted to use under considering `GPL V3` license. Clone the source code from [GitHub][101] at [medisys-fhir-launch][103] `git` repository.

```bash
git clone git@github.com:medisysco/medisys-fhir-launch.git
cd ./medisys-fhir-launch

```

Here is the maven repository of this project. add the repository to `.m2` `settings.xml` or `pom.xml`.


```xml
<server>
  <id>medisys.snapshots</id>
  <configuration>
      <httpHeaders>
        <property>
          <name>Authorization</name>
          <value>Basic bWF2ZW46bWF2ZW4=</value>
        </property>
      </httpHeaders>
  </configuration>
</server>

<repository>
    <id>medisys.releases</id>
    <url>http://maven.medisys.com.my/maven2/snapshots/</url>
    <releases>
        <enabled>true</enabled>
    </releases>
    <snapshots>
        <enabled>false</enabled>
    </snapshots>
</repository>
```

### LICENSE

```
Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is furnished
to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
```

### About [Medical Systems Co.][100]

`Medisys` is the short form of [Medical Systems Co.][100] is a Riyadh based company which provides full range offers through `HMIS` – Health Management Information Systems starting from analysing requirements, development, application delivery and full range of support for user adaptations. The evolution and migration to Oracle 10g database and `RAC` from previous architecture gave Medisys's `HMIS` (Hospital Management Information System) location transparency, redundancy and volatility on information management.


### About the Founder

[`Mohamad Al-Sherbini(Sherbini)`][201] is the founder of [Medical Systems Co.] [100]. [`Azizul Hoque Bhuyan(Aziz)`][202] is the Technical Manager as well as founder of Medisys (BD) Ltd and Medisys (MY) Ltd.


### About the Project

This project started aim to development HL7 Interoperability/Integration to add feature on `HMIS` (Hospital Management Information System). This project developed by java EE with `Apache Camel ESB` and `OpeneHealth IPF Tools`. [`Md Shahed Hossain(Shahed)`][203] is Technical Lead of this project.


### Contact

- [**info@medisys.com.my**][200]
- [**www.medisys.com.my**][100] 


[100]:  http://www.medisys.com.my "Medical Systems Co."
[101]:  https://github.com/ "GitHub"
[102]:  https://github.com/medisysco/medisys-fhir-parent "medisys-fhir-parent"
[103]:  https://github.com/medisysco/medisys-fhir-launch "medisys-fhir-launch"

[200]:  mailto:info@medisys.com.my "Medisys Contact"
[201]:  mailto:sherbini@medisys.com.my "Mohamad Al-Sherbini(Sherbini)"
[202]:  mailto:aziz@medisys.com.my "Azizul Hoque Bhuyan(Aziz)"
[203]:  mailto:shahed@medisys.com.my "Md Shahed Hossain(Shahed)"
