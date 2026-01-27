# X Clone Backend

---

This is the backend for my xclone that i made a long time ago. I am currently refactoring it, so all there is documentation wise is the local run instructions. Check back soon for a more in depth documentation.

---

## Running the project locally:

## Requirements
- Docker
- A Google OAuth Client ID & Credentials
- An S3 Bucket & Credentials

## Project Setup

1. Clone the project
```
git@github.com:jokerhutt/x-clone-backend.git
```
2. Navigate to project directory
```
cd /path/to/x-clone-backend
```
3. Copy & Paste the `.example.env` into a `.env` file in the project directory

4. Place your S3 credentials in the `.env`
5. Optionally adjust the `FRONTEND_ORIGINS` to match your frontend url.

6. Run the Application Container (Runs the application and a DB with the schema)
```
docker compose -f docker-compose.duoclone.yml build backend
docker compose -f docker-compose.duoclone.yml up -d backend
```

