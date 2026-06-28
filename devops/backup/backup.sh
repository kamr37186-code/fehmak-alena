#!/bin/bash
# Fahmak Alena - Automated Database Backup Script
# Schedule: Daily at 02:00 AM via cron/Docker

set -euo pipefail

# Configuration (via environment variables)
DB_HOST="${POSTGRES_HOST:-localhost}"
DB_PORT="${POSTGRES_PORT:-5432}"
DB_NAME="${POSTGRES_DB:-fahmak_alena}"
DB_USER="${POSTGRES_USER:-root}"
BACKUP_DIR="/backups"
RETENTION_DAYS="${BACKUP_RETENTION_DAYS:-30}"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="${BACKUP_DIR}/fahmak_alena_${TIMESTAMP}.sql.gz"

# Ensure backup directory exists
mkdir -p "${BACKUP_DIR}"

echo "[$(date)] Starting backup of ${DB_NAME}..."

# Perform compressed backup
PGPASSWORD="${POSTGRES_PASSWORD}" pg_dump \
  -h "${DB_HOST}" \
  -p "${DB_PORT}" \
  -U "${DB_USER}" \
  -d "${DB_NAME}" \
  --format=custom \
  --compress=9 \
  --verbose \
  -f "${BACKUP_FILE}"

echo "[$(date)] Backup created: ${BACKUP_FILE} ($(du -h ${BACKUP_FILE} | cut -f1))"

# Upload to cloud storage (Google Cloud Storage example)
if command -v gsutil &> /dev/null; then
  GCS_BUCKET="${GCS_BACKUP_BUCKET:-gs://fahmak-alena-backups}"
  gsutil cp "${BACKUP_FILE}" "${GCS_BUCKET}/daily/"
  echo "[$(date)] Uploaded to ${GCS_BUCKET}/daily/"
fi

# Cleanup old local backups
find "${BACKUP_DIR}" -name "fahmak_alena_*.sql.gz" -mtime +"${RETENTION_DAYS}" -delete
echo "[$(date)] Cleaned up backups older than ${RETENTION_DAYS} days."

echo "[$(date)] Backup completed successfully."
