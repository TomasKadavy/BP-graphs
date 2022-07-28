FROM node:18-alpine as node
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY ./ ./
RUN npm run build --prod

FROM nginx:alpine
COPY --from=node /app/dist/graph-app /usr/share/nginx/html
