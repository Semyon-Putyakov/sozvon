FROM node:20-alpine
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
# Открываем порт
EXPOSE 3000
CMD ["npm", "start"]
