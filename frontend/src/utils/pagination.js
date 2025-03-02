export const paginate = (items, currentPage, perPage) => {
  const start = (currentPage - 1) * perPage;
  const end = start + perPage;
  return items.slice(start, end);
};

export const getPaginationInfo = (items, currentPage, perPage) => {
  const totalItems = items.length;
  const totalPages = Math.ceil(totalItems / perPage);
  const startItem = (currentPage - 1) * perPage + 1;
  const endItem = Math.min(startItem + perPage - 1, totalItems);
  
  return { totalItems, totalPages, startItem, endItem };
}; 