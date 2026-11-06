# Svenska Sensorer – ATAK-CIV 5.6 (utvecklingsversion 0.1)

Bygger på användarens uppladdade officiella ATAK plugin template.

## Implementerat
- ATAK-verktygsknapp och panel enligt mallen
- 7 symboltyper × 6 färger (PNG-bibliotek)
- Förhandsvisning med rotation 0–359 grader och mils (6400/varv)
- Lokal lagring av val, vinkel och beteckning

## Inte implementerat
- Kartplacering, kartobjekt, flyttning, ritning av polygon/patrullväg, CoT-synkronisering
- Kompilerad och signerad APK; kompatibilitetstest med ATAK 5.6.0.13
- Verifiering av symbolgrafik mot Försvarsmaktens original

## Byggning
Mallen använder ATAK Gradle TakDev och behöver giltigt SDK/repository eller atak-gradle-takdev.jar enligt `app/build.gradle`. GitHub Actions-filen är en förberedelse och kräver rätt TAK-beroenden. Lägg inte SDK-hemligheter i Git.
