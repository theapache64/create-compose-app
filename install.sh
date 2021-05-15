echo "Downloading main JAR..." &&
wget -q "https://github.com/theapache64/create-compose-desktop-app/releases/latest/download/create-compose-desktop-app.main.jar" -O "create-compose-desktop-app.main.jar" --show-progress &&

echo "Moving files to ~/.create-compose-desktop-app" &&

mkdir -p ~/.create-compose-desktop-app &&
mv create-compose-desktop-app.main.jar ~/.create-compose-desktop-app/create-compose-desktop-app.main.jar

echo "Installing..." &&
echo "\nalias create-compose-desktop-app='java -jar ~/.create-compose-desktop-app/create-compose-desktop-app.main.jar'" >> ~/.bash_aliases &&

echo "Done"