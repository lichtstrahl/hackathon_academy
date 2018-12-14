# hackathon_academy
Хакатон. Android Academy 2018

В качестве API для музыки используем https://www.theaudiodb.com/api_guide.php
В качестве API_KEY берется 1

* Поиск исполнителя по имени: 

      theaudiodb.com/api/v1/json/{APIKEY}/search.php?s={Artist name}
* Список альбомов указанного артиста: 

      theaudiodb.com/api/v1/json/{APIKEY}/searchalbum.php?s={Artist name}

* Информация о песне конкретного исполнителя: 

      theaudiodb.com/api/v1/json/{APIKEY}/searchtrack.php?s={Artist_Name}&t={Single_Name}

* Информация об исполнителе по его id:

      theaudiodb.com/api/v1/json/{APIKEY}/artist.php?i={artistid}

# Альбомы
* Ииформация о кнокретном альбоме артиста:  

      theaudiodb.com/api/v1/json/{APIKEY}/searchalbum.php?s={Artist name}&a={Album name}
* Поиск альбома исключительно по его названию: 

      theaudiodb.com/api/v1/json/{APIKEY}/searchalbum.php?a={Album name}
*  Поиск альбома по его ID:

      theaudiodb.com/api/v1/json/{APIKEY}/album.php?m={albumid}
      




# Треки
* Возвращает все треки из альбома по ID альбома:

      theaudiodb.com/api/v1/json/{APIKEY}/track.php?m={albumid}

* Возвращает конкретный трек по его id:


      theaudiodb.com/api/v1/json/{APIKEY}/track.php?h={trackid}

# Картинки
При получении картинки, не обязательно грузить полностью большую. Можно получить маленькую,добавив **/preview**
Original Image - **theaudiodb.com/images/media/artist/thumb/xxtwus1340291734.jpg** <br>
Small Image - **theaudiodb.com/images/media/artist/thumb/xxtwus1340291734.jpg/preview**

Кажется это должно помочь с извлечением информации из mp3-файла
https://github.com/mpatric/mp3agic



# Альтернативный вариант:
      https://lyricsovh.docs.apiary.io/#
      http://api.lololyrics.com/

