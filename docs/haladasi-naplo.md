# Haladási napló

### 1. hét
Jelentkeztem a témára aminek még nincsen véglegesítve a neve

### 2. hét
Létrehoztam a repositorykat és a specifikációt is megírtam
A repok url-jei:
- https://github.com/pintertamas/befake-backend
- https://github.com/pintertamas/befake-mobile

### 3. hét
Adobe XD segítségével elkészítettem a képernyőképeket. Mivel a BeReal nevű alkalmazást fogom lemásolni, nem volt nehéz dolgom, csak ugyan azt meg kellett valósítnom mint az app, egy néhány számomra felesleges funkciót lecsípve.
A képernyőképeket ebben a mappában lehet részletesen megnézni (egyelőre nem csináltam regisztrációs és bejelentkezős felületet, de az is lesz majd):

```https://github.com/pintertamas/befake-backend/tree/main/docs/designs/artboards```

Az egészről pedig a terv itt látható:
![](https://github.com/pintertamas/befake-backend/blob/main/docs/designs/Screenshot%202022-09-17%20at%2021.34.53.png)

Elkészítettem az EK diagramot is, jelenlegi formájában így néz ki:
![](https://github.com/pintertamas/befake-backend/blob/main/docs/BeFake%20ER%20diagram.png?raw=true)

Az alkalmazás ezeket az endpointokat fogja tudni kiszolgálni:

```
GET:
- /user/{id} -> visszaadja egy felhasználó adatait
- /post/{postId} -> visszaadja a kért posztot
- /post/todaysPost/{userId} -> visszaadja a felhasználó aznapi posztját
- /storage/last14days/{userId} -> visszaadja a felhasználó utolsó 2 hetének posztjait
- /storage/{userId} -> visszaadjaa felhasználó addigi összes posztját

POST:
- /user/register -> regisztrál egy felhasználót
- /user/login -> beléptet egy felhasználót
- /user/edit/{id} -> szerkeszt egy felhasználót
- /friendlist/add/{userId} -> hozzáadja a kért barátot pending státusszal
- /friendlist/confirm/{userId} -> elfogadja a barát kérést és a pending státuszt aktívra változtatja
- /post/create -> készít egy posztot
- /post/edit/{id} -> szerkeszti a kért posztot
- /comment/create -> kommentel egyet
- /reaction/create -> reagál egyet

DELETE:
- /user/delete/{id} -> töröl egy felhasználót
- /friendlist/remove/{userId} -> töröl egy barátot
- /post/delete/{postId} -> töröl egy posztot
- /comment/delete/{commentId} -> töröl egy kommentet
- /reaction/delete/{reactionId} -> töröl egy reakciót
```
