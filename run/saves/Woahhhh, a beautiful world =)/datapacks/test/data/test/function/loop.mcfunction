execute unless block ~ ~ ~ #air run return run summon lightning_bolt
particle smoke ~ ~ ~ 0 0 0 0 1 force
scoreboard players remove #counter math 1
execute if score #counter math matches 1.. positioned ^ ^ ^0.1 run function test:loop