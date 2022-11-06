# Fight Regulator
Offers you more settings about PvP in your Minecraft server

## Documentation
Cet espace est rédigé en Français.

### Configuration
- `change-mode-cooldown` correspond au temps en secondes que doivent attendre les joueurs entre chaque changement de mode de PvP.
- `server-settings` est la section contenant les paramètres de PvP par défaut qui seront appliqués sur les mondes qui ont été configurés "par défaut" :
  - `enable-pvp` est `true` si le PvP est activé par défaut, sinon `false`.
  - `enable-player-choice` est `true` si les joueurs peuvent choisir s'ils activent le PvP ou non, `false` s'ils sont obligés de suivre le paramètre `enable-pvp`.
- `world-settings` est la section contenant les paramètres de PvP des différents mondes :
  - `<monde>` correspond à un nom de monde ("world", "world_nether", "world_the_end"...) :
    - `enable-pvp` est `true` si le PvP est activé par défaut, `false` s'il est désactivé ou `default` (ou non renseigné) s'il suit le paramètre du serveur.
    - `enable-player-choice` est `true` si les joueurs peuvent choisir s'ils activent le PvP ou non, `false` s'ils sont obligés de suivre le paramètre `enable-pvp` et `default` (ou non renseigné) s'il suit le paramètre du serveur.
- `messages` est la catégorie qui vous permet de configurer les différents messages.
  - `pvp-prohibited` est affiché dans la barre d'action du joueur.
  - Les autres messages sont affichés dans le tchat.

### Commandes
- `pvp` vous permet de changer de mode de PvP.
  - Utilisation : `/pvp <mode>`.
  - Les différents modes possibles sont `on`, `off` et `default`.
- `manage-pvp` vous permet de modifier certains paramètres de la configuration et des joueurs.
    - Utilisation : `/pvp <target> [...args]`.
    - Avec la cible `server`, qui vous permet de modifier les paramètres par défaut de PvP du serveur :
      - Utilisation : `/pvp server <enable-pvp> <enable-player-choice>`.
      - `enable-pvp` et `enable-player-choice` prennent comme valeurs `true` ou `false`.
    - La cible `world` change les paramètres de PvP d'un monde en particulier :
      - Utilisation : `/pvp world <world> <enable-pvp> <enable-player-choice>`.
      - `world` correspond au nom du monde que vous ciblez
      - `enable-pvp` et `enable-player-choice` prennent comme valeurs `true`, `false` ou `default`.
    - Avec la cible `player` vous permettant d'exécuter des actions sur un joueur :
      - Utilisation : `/pvp player <player> <action> [...args]`.
      - L'action `getmode` vous permet de connaître le mode de PvP actuel d'un joueur :
        - Utilisation : `/pvp player <player> getmode`.
      - Avec l'action `setmode` vous permettant de changer le mode d'un joueur :
        - Utilisation : `/pvp player <player> setmode <mode> [update-cooldown]`.
        - Les différents modes possibles sont `enabled`, `disabled` et `default`.
        - `update-cooldown` est optionnel, mais vous permet de mettre à jour le cooldown du joueur en le mettant à `true`.
      - L'action `getcooldown` vous permet de connaître le nombre de secondes que le joueur doit attendre avant son prochain changement de mode :
        - Utilisation : `/pvp player <player> getcooldown`.
      - Avec l'action `resetcooldown` qui remet à 0 le temps qu'un joueur doit attendre avant son prochain changement de mode :
        - Utilisation : `/pvp player <player> resetcooldown`.

### Permissions
- `fightregulator.change-mode`
  - Autorise l'accès à la commande `/pvp`.
  - Par défaut, elle est accessible par tout le monde.
- `fightregulator.bypass-cooldown`
  - Ignore le cooldown lors de l'exécution de la commande `/pvp`.
  - Par défaut, elle est donnée aux opérateurs.
- `fightregulator.manage`
  - Autorise l'accès à la commande `/manage-pvp`.
  - Par défaut, elle est donnée aux opérateurs.
- `fightregulator.manage-server`
  - Autorise l'accès aux cibles `server` et `world` dans la commande `/manage-pvp`.
  - Par défaut, elle est donnée aux opérateurs.
- `fightregulator.manage-players`
    - Autorise l'accès à la cible `player` dans la commande `/manage-pvp`.
    - Par défaut, elle est donnée aux opérateurs.
