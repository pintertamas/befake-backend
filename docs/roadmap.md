# Roadmap

- [X] specko befejezes kepernyokepekkel
- [X] sema ek diagrammal
- [X] rest api kitalalasa

- [X] témakiírás címének véglegesítése
- [X] tervezési döntések dokumentálása
- [X] spring oldalon daok, repo reteg es controller reteg (service retegnel mit akarok szetvalasztani? - mi menjen kulon kontenerekben kulon skalazodassal)

- [X] témakiírás szövegének véglegesítése
- [ ] megírni az egészet

---

- [ ] android alkalmazás


GET:
- [X] /user/{id} -> visszaadja egy felhasználó adatait
- [ ] /post/{postId} -> visszaadja a kért posztot
- [ ] /post/todaysPost/{userId} -> visszaadja a felhasználó aznapi posztját
- [ ] /post/lastPosts/{userId}/{days} -> visszaadja a felhasználó utolsó X napjának posztjait - itt az X egy paraméter lesz, amit ha nem adunk meg akkor az alapértelmezett érték 14 lesz
- [X] /post/{userId} -> visszaadjaa felhasználó addigi összes posztját

POST:
- [X] /user/register -> regisztrál egy felhasználót
- [X] /auth/login -> beléptet egy felhasználót
- [ ] /friendlist/add/{userId} -> hozzáadja a kért barátot pending státusszal
- [ ] /friendlist/confirm/{userId} -> elfogadja a barát kérést és a pending státuszt aktívra változtatja
- [X] /post/create -> készít egy posztot
- [ ] /comment/create -> kommentel egyet
- [ ] /reaction/create -> reagál egyet

PATCH:
- [ ] /user/{id} -> szerkeszt egy felhasználót
- [X] /post/{id} -> szerkeszti a kért poszt leírását

DELETE:
- [ ] /user/{id} -> töröl egy felhasználót
- [ ] /friendlist/{userId} -> töröl egy barátot
- [ ] /post/{postId} -> töröl egy posztot
- [ ] /comment/{commentId} -> töröl egy kommentet
- [ ] /reaction/{reactionId} -> töröl egy reakciót

