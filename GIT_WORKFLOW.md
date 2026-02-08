# 🔧 Git Workflow Guide for VeriCV

## Initial Setup

### 1. Add Files to Your Repo

```bash
# Navigate to your local VeriCV repo
cd /path/to/VeriCV

# Copy all the generated files into your repo
# (backend/, docker-compose.yml, *.md files, etc.)

# Check what's new
git status

# Add all files
git add .

# Commit with a meaningful message
git commit -m "feat: Initial project setup with Spring Boot backend structure"

# Push to GitHub
git push origin main
```

---

## Branch Strategy

### Create Feature Branches

```bash
# Create and switch to a new branch for authentication
git checkout -b feature/authentication

# Work on your feature...
# Make commits...

# Push feature branch
git push origin feature/authentication

# When done, merge back to main
git checkout main
git merge feature/authentication
git push origin main
```

### Recommended Branches

```bash
# Authentication work
git checkout -b feature/authentication

# CV management
git checkout -b feature/cv-management

# Digital signatures
git checkout -b feature/digital-signatures

# Verification system
git checkout -b feature/verification

# Frontend
git checkout -b feature/frontend

# Bug fixes
git checkout -b bugfix/fix-login-issue
```

---

## Commit Message Convention

Use semantic commit messages:

```bash
# New features
git commit -m "feat: Add user registration endpoint"
git commit -m "feat: Implement JWT authentication"
git commit -m "feat: Add CV creation functionality"

# Bug fixes
git commit -m "fix: Resolve database connection timeout"
git commit -m "fix: Correct JWT token expiration"

# Documentation
git commit -m "docs: Update README with setup instructions"
git commit -m "docs: Add API documentation"

# Code refactoring
git commit -m "refactor: Improve error handling in AuthService"
git commit -m "refactor: Extract signature logic to utility class"

# Tests
git commit -m "test: Add unit tests for UserService"
git commit -m "test: Add integration tests for authentication"

# Configuration
git commit -m "chore: Update Maven dependencies"
git commit -m "chore: Configure Docker Compose for PostgreSQL"
```

---

## Daily Workflow

### Start of Day

```bash
# Make sure you're on main
git checkout main

# Get latest changes
git pull origin main

# Create/switch to your working branch
git checkout -b feature/your-feature
# OR
git checkout feature/your-feature
```

### During Development

```bash
# Check what you've changed
git status

# See specific changes
git diff

# Add specific files
git add backend/src/main/java/com/vericv/controller/AuthController.java

# Or add all changes
git add .

# Commit with message
git commit -m "feat: Add login endpoint"

# Push to remote
git push origin feature/your-feature
```

### End of Day

```bash
# Commit your work
git add .
git commit -m "wip: Working on CV service"
git push origin feature/your-feature
```

---

## Common Commands

### Status & Info

```bash
# Check current status
git status

# View commit history
git log --oneline

# See differences
git diff

# See branches
git branch -a
```

### Undoing Changes

```bash
# Discard changes in a file
git checkout -- filename

# Unstage a file
git reset HEAD filename

# Undo last commit (keep changes)
git reset --soft HEAD~1

# Undo last commit (discard changes)
git reset --hard HEAD~1
```

### Branching

```bash
# List all branches
git branch -a

# Create new branch
git branch feature/new-feature

# Switch to branch
git checkout feature/new-feature

# Create and switch in one command
git checkout -b feature/new-feature

# Delete local branch
git branch -d feature/old-feature

# Delete remote branch
git push origin --delete feature/old-feature
```

### Merging

```bash
# Merge feature into main
git checkout main
git merge feature/authentication

# If conflicts occur, resolve them then:
git add .
git commit -m "merge: Resolve conflicts"
```

---

## Gitignore Setup

Your `.gitignore` should include:

```gitignore
# IDE
.idea/
.vscode/
*.iml

# Build
target/
*.class

# Environment
.env
.env.local

# Logs
*.log

# OS
.DS_Store
Thumbs.db
```

---

## Common Scenarios

### Scenario 1: Added Wrong Files

```bash
# If you accidentally added files
git reset HEAD file-to-remove

# If you already committed
git reset --soft HEAD~1
# Fix the files
git add .
git commit -m "correct commit message"
```

### Scenario 2: Need to Switch Branches Quickly

```bash
# Stash your changes
git stash

# Switch branches
git checkout other-branch

# Come back and restore
git checkout your-branch
git stash pop
```

### Scenario 3: Update from Main

```bash
# From your feature branch
git checkout main
git pull origin main

git checkout feature/your-feature
git merge main

# Resolve any conflicts
```

---

## Best Practices

1. **Commit Often**: Small, frequent commits are better than large ones
2. **Meaningful Messages**: Write clear commit messages
3. **Pull Before Push**: Always pull latest changes before pushing
4. **Test Before Commit**: Make sure code works before committing
5. **Don't Commit Secrets**: Never commit passwords, API keys, etc.
6. **Use Branches**: One feature = one branch
7. **Review Changes**: Always review with `git diff` before committing

---

## Example Workflow for Your Project

### Week 1: Authentication Feature

```bash
# Day 1: Setup
git checkout -b feature/authentication
# ... work on setup ...
git add .
git commit -m "feat: Add User entity and repository"
git push origin feature/authentication

# Day 2: Auth Service
# ... implement AuthService ...
git add .
git commit -m "feat: Implement user registration service"
git push origin feature/authentication

# Day 3: Auth Controller
# ... implement AuthController ...
git add .
git commit -m "feat: Add authentication endpoints"
git push origin feature/authentication

# Day 4: Testing
# ... test endpoints ...
git add .
git commit -m "test: Add tests for authentication"
git push origin feature/authentication

# Day 5: Merge to main
git checkout main
git merge feature/authentication
git push origin main
```

---

## Quick Reference

| Command | Purpose |
|---------|---------|
| `git status` | Check status |
| `git add .` | Stage all changes |
| `git commit -m "message"` | Commit with message |
| `git push` | Push to remote |
| `git pull` | Pull from remote |
| `git checkout -b branch` | Create new branch |
| `git merge branch` | Merge branch |
| `git log` | View history |
| `git diff` | See changes |
| `git stash` | Temporarily save changes |

---

## Troubleshooting

### Can't Push

```bash
# Usually need to pull first
git pull origin main
# Resolve any conflicts
git push origin main
```

### Merge Conflicts

```bash
# Git will mark conflicts in files
# Open files and fix conflicts
# Look for <<<<<<, ======, >>>>>>
# After fixing:
git add .
git commit -m "merge: Resolve conflicts"
```

### Accidentally Committed to Main

```bash
# Create a feature branch from current state
git branch feature/accidental-work

# Reset main
git reset --hard origin/main

# Switch to feature branch
git checkout feature/accidental-work
```

---

## Remember

- **Commit early, commit often**
- **Push to remote daily** (backup!)
- **Use meaningful branch and commit names**
- **Never commit sensitive data**
- **Review changes before committing**

Happy coding! 🚀
