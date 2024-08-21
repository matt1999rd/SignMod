import os
import re

def find_method_usages(directory, class_name, method_name):
    usages = []

    # Expression régulière pour trouver les appels de la méthode
    pattern = re.compile(r'\b' + re.escape(class_name) + r'\.' + re.escape(method_name) + r'\s*\(')

    # Parcourir récursivement les fichiers dans le répertoire donné
    for root, _, files in os.walk(directory):
        for file in files:
            if file.endswith(".java"):
                file_path = os.path.join(root, file)
                with open(file_path, 'r', encoding='utf-8') as f:
                    try:
                        content = f.read()
                        for match in re.finditer(pattern, content):
                            # Enregistrer l'utilisation avec le fichier et la ligne correspondante
                            line_number = content.count('\n', 0, match.start()) + 1
                            usages.append({
                                'file': file_path,
                                'line': line_number,
                                'context': content[match.start():content.find('\n', match.start())]
                            })
                    except UnicodeDecodeError:
                        # Ignorer les fichiers avec des erreurs de décodage
                        print(f"Erreur de décodage pour le fichier: {file_path}")

    return usages

# Exemple d'utilisation
directory = '/home/matthieu/.gradle/caches/forge_gradle/minecraft_user_repo/net/minecraftforge/forge/1.17.1-37.1.1_mapped_official_1.17.1/forge-1.17.1-37.1.1_mapped_official_1.17.1.jar'
class_name = 'RenderSystem'
method_name = 'getShader'
usages = find_method_usages(directory, class_name, method_name)
for usage in usages:
    print(f"Fichier: {usage['file']}, Ligne: {usage['line']}, Contexte: {usage['context']}")