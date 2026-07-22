#!/usr/bin/env bash
#Перед первом deploy создай файл sudo nano /etc/systemd/system/danshin_notice_api.service
#и скопируй текст из файла (или файл) danshin_notice_api.service,
# который находится в корне проекта.
#Затем добавь в автозапуск systemctl enable danshin_notice_api.
#Для удаления используй systemctl disable danshin_notice_api.

#Добавь в config файл apache ограничения по ip

echo "Собрал проект? y/n"
read answer
if [ $answer == "y" ]; then
    echo "копирую файлы на сервер..."

    ssh root@146.0.78.87 mkdir /var/www/app
    ssh root@146.0.78.87 mkdir /var/www/app/danshin_notice
    ssh root@146.0.78.87 rm -r /var/www/app/danshin_notice/api
    ssh root@146.0.78.87 mkdir /var/www/app/danshin_notice/api
    scp target/danshin_notice_api-1.0.0.jar root@146.0.78.87:/var/www/app/danshin_notice/api
    ssh root@146.0.78.87 mv /var/www/app/danshin_notice/api/danshin_notice_api-1.0.0.jar /var/www/app/danshin_notice/api/app.jar

    ssh root@146.0.78.87 "systemctl restart danshin_notice_api"
    ssh root@146.0.78.87 "systemctl status danshin_notice_api"

    echo "Успешно завершено!"
else
    echo "Ну, тогда собирай"
fi
