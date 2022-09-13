## Specifikáció

Az alkalmazásom egy Spring Bootban írt backendből és egy Kotlinban írt Androidos frontendből fog állni, célja pedig a skálázhatóság demonstrálása lesz.
Terveim szerint az alkalmazás megvalósításához microservice-eket fogok használni, majd kubernetes segítségével deployolom a konténerizált serviceket egy hosting szolgáltatásra.
Az alkalmazás, amin a témát bemutatom a BeReal-hez hasonló, képek megosztására alkalmas social media platform lesz.
Lehetősége lesz a felhasználóknak regisztrálásra, bejelentkezésre és napi 1 kép feltöltésére is, ezek mellett pedig be lehet jelölni ismerősöket,
akiknek a képeit végig tudjuk görgetni és kommentelni is lehet alájuk. Ami miatt ez az app jól fogja demonstrálni a skálázhatóságot az az,
hogy minden felhasználó a nap egy bizonyos pontján fog kapni egy értesítést, ami arra kéri, hogy akkor rakja ki a képét.
Ez azt eredményezni, hogy a szerverek hirtelen kaphatnak majd nagy terhelést, amire képesnek kell lenniük.
Ahhoz, hogy a terhelhetőség mértékét vizsgáljam, load testeket fogok alkalmazni.

A technológiák, amiket használni fogok és az ismeretségem velük (1-3 skálán):

- Spring Boot                 - 3
- Kotlin                      - 2
- Docker                      - 2
- Kubernetes                  - 1
- Microservices architecture  - 2
- PostgreSQL                  - 3

![]([https://www.theguardian.com/media/2022/aug/02/bereal-the-instagram-rival-app-wants-me-to-be-authentic-i-cant-imagine-anything-more-boring](https://i.guim.co.uk/img/media/6a1c50677e857d9520af6c96b00caea78a094ecb/0_0_2500_1500/master/2500.jpg?width=880&quality=85&fit=max&s=040b6bcaaddc82647bd1975566c4b3a3))
