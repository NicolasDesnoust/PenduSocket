PenduSocket.
Ce dépôt contient trois packages dans lequels il est possible d'expérimenter différentes versions du jeu du Pendu orienté client/serveur.

Package clientUnique:
Dans cette version, un seul client peut jouer à la fois sur le serveur. Le serveur peut faire jouer un nombre de clients limité.

Package clientsMultiples:
Dans cette version, plusieurs clients peuvent jouer en simultané sur le serveur. Le serveur peut faire jouer un nombre de clients limité.

Package clientsDuels:
Dans cette version, plusieurs clients peuvent jouer en simultané en 1 contre 1 sur le serveur. Le serveur peut organiser un nombre de duels limité. Lors d'un duel les deux clients jouent chacun leur tour et le premier à avoir deviné le mot gagne la partie. Par soucis d'équité si le premier joueur trouve le mot, le second a le droit a un dernier essai.

N.B.
Pour tester une version il faut compiler les classes du package (javac ...) et lancer le serveur (java <nom_du_serveur>) avant de lancer un ou plusieurs clients (java <nom_du_client>).
