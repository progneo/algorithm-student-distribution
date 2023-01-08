package util

import data.model.Project

fun containsGroup(project: Project, groups: List<String>): Boolean {
    for (g in project.groups) {
        if (groups.map { it.lowercase() }.contains(g.lowercase())) return true
    }
    return false
}