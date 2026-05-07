function toIsoDateTime(value) {
  return value ? new Date(value).toISOString() : null
}

function formatDateTime(value) {
  return new Intl.DateTimeFormat('es-ES', {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(new Date(value))
}

export { formatDateTime, toIsoDateTime }
