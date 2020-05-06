# ChotuveMobileApp

En la descripción de este repositorio se encuentra el link de descarga a las
las APK´s pertenecientes al entorno de staging y de produción. Solo basta con
correlas en un emulador o en tu propio dispositivo andriod.

## Testing

a. Mediante `android studio` utilizando un emulador.

b. Mediante un dispositivo android:

  1. Activar el Modo USB Debuggin, ubicado en configuracion del sistema.
  2. Conectar tu dispositivo a la computadora mediante un cable USB.
  3. Instalar la herramienta `adb`. En linux `$ sudo apt install adb`.
  4. Insalar APK con el comando `$ adb install <apk.name>`.

### Conectar tu dispositovo a localhost de la PC

Es importante que ambos esten conectados mediante WiFi.

Debemos configurar `BASE_URL` de la app con nuestra `https://<ip>:port` en
donde tenemos corriendo el server que le queremos pegar.

**Nota**: podemos obtener la IP, en Linux: `$ ifconfig | grep "inet " | grep -v 127.0.0.1`

### Otras utilidades

Mediante adb podemos ver los logs de la app: `adb logcat | grep <log_tag>`
