export const paginate = (items, currentPage, perPage) => {
  const start = (currentPage - 1) * perPage;
  const end = start + perPage;
  return items.slice(start, end);
};

export const getPaginationInfo = (items, pagination) => {
  const { currentPage, perPage } = pagination

  const totalItems = items.length;
  const totalPages = Math.ceil(totalItems / perPage);
  const startItem = (currentPage - 1) * perPage + 1;
  const endItem = Math.min(startItem + perPage - 1, totalItems);

  return { totalItems, totalPages, startItem, endItem };
}

/**
 * paginate with the given state
 * @param {import('vue').Ref} state
 */
export const __paginate = (state) => {
  const start = (state.pagination.currentPage - 1) * state.pagination.perPage
  const end = start + state.pagination.perPage
  return state.filtered.slice(start, end)
}
