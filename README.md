# Lab 2: Mobile Tariffs (XML, XSD, XSL)

Java 21 / Maven application for variant 3 (mobile operator tariffs). It

1. validates `tariffs.xml` against `tariffs.xsd`;
2. parses it with three parsers (SAX, DOM, StAX) into a list of `Tariff` objects;
3. sorts the list with `Comparator`s;
4. transforms the document with XSL into a structure grouped by operator.

## Requirements

- JDK 21
- Maven 3.9+

## Build, test, run

    mvn clean verify          # compile, run tests, check coverage
    mvn compile exec:java     # run the application with the default files

Custom files:

    mvn compile exec:java -Dexec.args="input.xml output.xml"

Defaults: input `src/main/resources/tariffs.xml`, output `target/output/tariffs-by-operator.xml`.
The process exits with code 1 if the input is invalid.

The coverage report is written to `target/site/jacoco/index.html`.
The build fails if line coverage is below 100% or branch coverage is below 90%
(generated JAXB classes are excluded).

## Project layout

| Path | Purpose |
| --- | --- |
| `src/main/resources/tariffs.xsd` | Schema: simple and complex types, enumerations, limits, required attributes, `xs:ID` |
| `src/main/resources/tariffs.xml` | Sample data (6 tariffs, 3 operators) |
| `src/main/resources/tariffs-by-operator.xsl` | XSLT 1.0 grouping tariffs by operator |
| `src/main/resources/logback.xml` | Console and rolling file logging (`logs/tariffs.log`) |
| `src/main/xjb/bindings.xjb` | Renames generated JAXB classes (`TariffType` to `Tariff`) |
| `ua.lab2.tariffs.parser` | `SaxTariffParser`, `DomTariffParser`, `StaxTariffParser` and their shared code |
| `ua.lab2.tariffs.sort` | `TariffComparator`: sort by name, operator, payroll, SMS price, operator then payroll |
| `ua.lab2.tariffs.xml` | `XmlValidator` (XSD) and `XslTransformer` (XSL) |
| `ua.lab2.tariffs` | `TariffApp` (workflow) and `Main` (command line) |

The model classes (`Tariff`, `CallPrices`, `Parameters`, `Tarification`, `Status`) are generated
from the XSD by xjc during `mvn compile` into `target/generated-sources/xjc` and are not committed.

## Data model

A `Tariff` has the attributes `id` (`xs:ID`, pattern `tariff-N`) and `status` (`ACTIVE`, `ARCHIVED`),
and the elements:

- `Name`, `OperatorName`
- `Payroll` (monthly fee, UAH, 0 or more)
- `CallPrices`: `Inside`, `Outside`, `Landline` (UAH per minute, 0 or more)
- `SmsPrice` (UAH, 0 or more)
- `Parameters`: `FavoriteNumbers` (0 or more), `Tarification` (`TWELVE_SECOND`, `PER_MINUTE`),
  `ConnectionFee` (UAH, 0 or more)

## XSL result

The flat list of tariffs is split into sub-lists, one element per operator:

    <Tariffs xmlns="http://lab2.ua/tariffs">
        <Kyivstar>
            <Tariff id="tariff-1" status="ACTIVE"> ... </Tariff>
            <Tariff id="tariff-2" status="ACTIVE"> ... </Tariff>
        </Kyivstar>
        <Vodafone> ... </Vodafone>
        <lifecell> ... </lifecell>
    </Tariffs>

Spaces in an operator name are replaced with `_` in the element name.
