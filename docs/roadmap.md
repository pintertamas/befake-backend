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

---

Endpointok állapotai:

GET:
- [X] ```/user/{id} ``` -> visszaadja egy felhasználó adatait
- [X] ```/post/{fileName} ``` -> visszaadja a kért képet
- [X] ```/post/today/{userId} ``` -> visszaadja a felhasználó aznapi posztját
- [X] ```/post/lastPosts/{userId}/{days} ``` -> visszaadja a felhasználó utolsó X napjának posztjait - itt az X egy paraméter lesz, amit ha nem adunk meg akkor az alapértelmezett érték 14 lesz
- [X] ```/post/user/{userId} ``` -> visszaadjaa felhasználó addigi összes posztját
- [X] ```/friendlist ``` -> visszaadjaa felhasználó barátait
- [X] ```/friendlist/pending ``` -> visszaadjaa felhasználó bejövő barátkéréseit

POST:
- [X] ```/user/register ``` -> regisztrál egy felhasználót
- [X] ```/auth/login ``` -> beléptet egy felhasználót
- [X] ```/friendlist/add/{userId} ``` -> hozzáadja a kért barátot pending státusszal
- [X] ```/post/create ``` -> készít egy posztot
- [ ] ```/comment/create ``` -> kommentel egyet
- [ ] ```/reaction/create ``` -> reagál egyet

PATCH:
- [ ] ```/user/{id} ``` -> szerkeszt egy felhasználót
- [X] ```/post/{id} ``` -> szerkeszti a kért poszt leírását
- [X] ```/friendlist/accept/{userId} ``` -> elfogadja a barát kérést és a pending státuszt aktívra változtatja

DELETE:
- [ ] ```/user/{id} ``` -> töröl egy felhasználót
- [X] ```/friendlist/reject/{userId} ``` -> töröl egy barátot
- [X] ```/post/{postId} ``` -> töröl egy posztot
- [ ] ```/comment/{commentId} ``` -> töröl egy kommentet
- [ ] ```/reaction/{reactionId} ``` -> töröl egy reakciót

notes>
- user serviceben a user törlése küldjön kérést a friend servicenek hogy törölje a meglévő barátságokat
- user regisztrálásra email service emailt küld (esetleg az is a notificationbe megy és arra figyel az email service és küldi az emailt)
- poszt kreálásnál mehet az üzenet kafkának és erre megy az értesítés a barátoknak
- egy szerver ami a random időpontot generálja (egy darab instance lehet csak belőle)
- barát kérésre is mehet az üzenet meg komment, reakció stb
- profilkép feltöltő endpoint kell ami s3-ba kiteszi a képet
- a remove post is hívjon át az interactionbe és törölje a poszthoz tartozó dolgokat