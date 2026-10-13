export default function PriorityBadge({ priority }) {
  if (!priority) {
    return <span className="badge priority-unknown">Pending</span>;
  }
  return <span className={`badge priority-${priority.toLowerCase()}`}>{priority}</span>;
}
