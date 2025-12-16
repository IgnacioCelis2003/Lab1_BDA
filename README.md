# Lab1_BDA
Laboratorio N°1 de Base de datos avanzada

Para cloner el repositorio en su computador, abra una consola ya sea cmd o algun powershell e ingrese git clone https://github.com/IgnacioCelis2003/Lab1_BDA.git

Una vez clonado el repositorio, abra el programa que tiene para ver la base de datos (ya sea pgAdmin 4 u otros), una vez dentro del programa, se debe crear una base de datos llamado Lab1BDA. 

Con la base de datos creada, se debe abrir una ventana de consulta Query, una vez dentro de la pestaña se debe abrir el archivo llamado BD.sql copiar todo su contenido, pegarlo en la pestaña de consuta Query y ejecutarlo. Si da algun error con respecto a malformacion de tablas o conflictos entre tablas, agregar uno por uno los create.

Ya creadas las tablas, abrir el archivo Datos_prueba.sql copiar los insert into *tabla*() values() uno por uno, hacerlos de a más lleva a errores, pegarlos en la pestaña Query y ejecutarlo.


Luego de rellenar las tablas con los datos se puede ejecutar el programa. Abrir intellij IDEA, buscar la carpeta del proyeacto, buscar la carpeta Backend y aprirla. Una vez abierta se busca el archivo BackendApplication, se abre y se ejecuta (revisar que a la izquierda del boton de ejecutar diga "current file" o "BackendApplication"). Para ejecutar el frontend se abre Visual Studio Code, se busca la carpeta del proyeacto, buscar la carpeta Frontend_Nuxt y abrirla. Para ejecutar el frontend se debe abrir una terminal en Visual Studio Code y ejecutar el comando npm i (isntalará todas las dependencias necesarias), una vez instaladas todas las dependencias, ejecutar el comando npm run dev, esperar a que cargue y hacer crtl + click en http://localhost:3000/ esto llevará a la página.