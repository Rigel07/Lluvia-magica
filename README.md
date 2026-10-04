# Lluvia Mágica (Forge 1.20.1)

De golpe empieza a llover… **algodón de azúcar y gominolas**. Y bajo esa lluvia pueden aparecer **mobs de otros mods**.

## Cómo empieza la lluvia
- **Varita de lluvia mágica**: clic derecho = empieza. Agachado + clic derecho = para. (Receta: azúcar, fragmento de amatista y palos.)
- **Comandos** (op): `/lluviamagica start [segundos]`, `/lluviamagica stop`, `/lluviamagica status`.
- **Sola, de golpe**: por defecto un 2 % por minuto en el Overworld, y un 10 % de las lluvias normales se vuelven mágicas.

## Qué cae
Seis golosinas comestibles, con un pequeño efecto cada una:
| Golosina | Efecto |
|---|---|
| Algodón de azúcar rosa | Velocidad |
| Algodón de azúcar azul | Caída lenta |
| Gominola roja | Regeneración |
| Gominola verde | Salto mejorado |
| Gominola amarilla | Suerte |
| Gominola morada | Absorción |

Además, la lluvia se ve con partículas de colores y el cielo se tiñe de rosa.

## Mobs de otros mods
Cerca de cada jugador, bajo cielo abierto, aparecen mobs de **cualquier mod instalado** (excepto Minecraft vanilla).
Todo se ajusta en `config/lluviamagica-common.toml`:
- `whitelist` / `blacklist`: `"modid:mob"` o `"modid:*"` para todo un mod.
- `includeMonsters` (por defecto `false`): permite también mobs hostiles.
- `includeVanillaMobs` (por defecto `false`): ponlo en `true` para probar sin otros mods.
- `despawnMobsWhenRainEnds`: al acabar, los mobs de la lluvia desaparecen (salvo nombrados, domesticados o atados).
- Duración, probabilidades, intervalos y límites (para evitar lag).

## Compilar con GitHub
1. Sube **todo** el contenido de esta carpeta a un repositorio de GitHub.
2. Pestaña **Actions** → último workflow → **Artifacts** → `lluvia-magica-jar`.
3. Dentro del zip está `lluviamagica-1.0.0.jar`: ponlo en la carpeta `mods` de Forge 1.20.1.

Localmente: Java 17 + Gradle 8.1.1 → `gradle build` (el .jar queda en `build/libs`).
