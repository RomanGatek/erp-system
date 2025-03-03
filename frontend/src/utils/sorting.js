export const setupSort = (field) => {
  return { field, direction: 'asc' }
}

export const sort = (state, filtered) => {
  if (state.sorting.field !== 'actions') {
    filtered.sort((a, b) => {
      const aVal = a[state.sorting.field]
      const bVal = b[state.sorting.field]
      const direction = state.sorting.direction === 'asc' ? 1 : -1
      return aVal > bVal ? direction : -direction
    })
  }
  return filtered;
}
