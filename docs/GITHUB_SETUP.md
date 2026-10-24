# GitHub Setup — Beginner Steps

## Option A: Upload the prepared files through the GitHub website

1. Sign in to GitHub.
2. Create a new repository named `smart-campus-food-rescue-nutrition-system`.
3. Choose **Private** while this is a student project with evolving requirements; it can be made public later.
4. Do not add a README in GitHub if you plan to upload this folder's README, so you avoid a merge/conflict on the first upload.
5. Download/extract the prepared project ZIP.
6. Open the new repository page and choose **Add file → Upload files**.
7. Upload the contents of the extracted project folder (including the README, `docs`, `pom.xml`, `src`, and `.gitignore`), then commit with a message such as `Add initial Java console prototype and project documentation`.

## Option B: Use Git on your computer

Open a terminal in the extracted project folder and run:

```bash
git init
git add .
git commit -m "Add initial Java console prototype and project documentation"
git branch -M main
```

Create an empty repository on GitHub, then copy the repository URL GitHub shows and run:

```bash
git remote add origin <PASTE-YOUR-GITHUB-REPOSITORY-URL-HERE>
git push -u origin main
```

Replace the placeholder with your own URL. Do not paste a password or personal access token into project files or chat.

## Safe commit routine

After each completed feature and passing tests:

```bash
git status
git add .
git commit -m "Describe the feature or fix"
git push
```

Before every commit, inspect `git status` and do not commit actual student records, room numbers, private wellness responses, passwords, API keys, or campus credentials.

## Important

This guide prepares the local files for GitHub. It does not mean a live GitHub repository has already been created. A connected GitHub integration may be used to create/publish it directly after the account is connected and permissions are confirmed.
