package com.cargoexpress.app.core.presentation.auth.register

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Política de Privacidad", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "CargoExpress",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Fecha de modificación: 07 de Julio del 2026",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            PrivacyParagraph(
                "CargoSystems, responsable del desarrollo y operación de la plataforma CargoExpress, se compromete a proteger la privacidad de los usuarios que utilizan la plataforma. " +
                "La presente Política de Privacidad describe cómo se recopila, utiliza, almacena y protege la información personal y operativa de los usuarios, en concordancia con lo establecido en el Acuerdo de Servicio SaaS.\n\n" +
                "El uso de la plataforma implica la aceptación de las disposiciones aquí descritas. Si el usuario no está de acuerdo con esta política, deberá abstenerse de utilizar el servicio."
            )

            PrivacySectionTitle("Responsable del Tratamiento de Datos")
            PrivacyParagraph(
                "CargoSystems, a través de la plataforma CargoExpress, es responsable del tratamiento de los datos personales y operativos recopilados durante el uso del servicio. " +
                "Las consultas relacionadas con esta política pueden dirigirse mediante los canales oficiales publicados en la sección \"Contacto\" del website."
            )

            PrivacySectionTitle("Datos que Recopilamos")
            PrivacyParagraph(
                "Durante el registro y uso de la plataforma, CargoExpress recopila los siguientes tipos de información, según el rol del usuario:\n\n" +
                "Datos comunes de cuenta:\n" +
                "• Correo electrónico.\n" +
                "• Contraseña (almacenada de forma cifrada).\n" +
                "• Número de teléfono.\n\n" +
                "Datos del usuario con rol Empresario:\n" +
                "• Nombre de la empresa.\n" +
                "• Dirección.\n\n" +
                "Datos del usuario con rol Cliente:\n" +
                "• DNI.\n" +
                "• Fecha de nacimiento.\n\n" +
                "Datos operativos generados durante el uso del servicio:\n" +
                "• Información de viajes registrados (origen, destino, fechas, estado).\n" +
                "• Ubicación referencial de vehículos durante el seguimiento de viajes.\n" +
                "• Registros de gastos asociados a los viajes.\n" +
                "• Datos de vehículos y conductores registrados por el empresario.\n" +
                "• Historial de auditoría de cambios realizados en la plataforma."
            )

            PrivacySectionTitle("Finalidad del Tratamiento de Datos")
            PrivacyParagraph(
                "Los datos recopilados son utilizados exclusivamente para los siguientes fines:\n\n" +
                "• Crear y administrar la cuenta del usuario.\n" +
                "• Prestar las funcionalidades principales de la plataforma (registro de viajes, seguimiento, gastos, gestión de conductores y vehículos).\n" +
                "• Generar reportes y resúmenes operativos para empresarios y clientes.\n" +
                "• Mantener el registro de auditoría de cambios dentro del sistema.\n" +
                "• Enviar comunicaciones relacionadas con el servicio.\n" +
                "• Cumplir con obligaciones legales aplicables.\n\n" +
                "CargoExpress no utiliza los datos personales del usuario con fines publicitarios ni los comparte con terceros para fines comerciales ajenos a la prestación del servicio."
            )

            PrivacySectionTitle("Almacenamiento de Datos en la Nube")
            PrivacyParagraph(
                "Los datos del usuario, incluyendo la información de cuenta y los datos operativos (viajes, gastos, vehículos, conductores), se almacenan en una base de datos MySQL alojada en la infraestructura en la nube de Railway.\n\n" +
                "CargoSystems adopta las siguientes medidas respecto al almacenamiento en la nube:\n\n" +
                "• Las conexiones a la base de datos se realizan mediante canales encriptados.\n" +
                "• Las credenciales y secretos de acceso a la infraestructura se gestionan mediante variables de entorno, sin exponerse en el código fuente.\n" +
                "• La contraseña del usuario se almacena de forma cifrada, nunca en texto plano.\n" +
                "• Railway, como proveedor de infraestructura, mantiene sus propias medidas de seguridad y disponibilidad sobre los servidores donde se aloja la información."
            )

            PrivacySectionTitle("Conservación de los Datos")
            PrivacyParagraph(
                "Los datos del usuario se conservarán mientras la cuenta permanezca activa en la plataforma. En caso de que el usuario solicite la eliminación de su cuenta, CargoSystems procederá a eliminar o anonimizar los datos personales, " +
                "salvo aquella información que deba conservarse por obligación legal o para la resolución de disputas relacionadas con servicios ya prestados."
            )

            PrivacySectionTitle("Derechos del Usuario sobre sus Datos")
            PrivacyParagraph(
                "El usuario tiene derecho a:\n\n" +
                "• Acceder a los datos personales que CargoExpress ha registrado sobre él.\n" +
                "• Solicitar la corrección de datos inexactos o desactualizados.\n" +
                "• Solicitar la eliminación de su cuenta y datos personales, conforme a lo indicado en la sección anterior.\n" +
                "• Retirar su consentimiento para el tratamiento de datos, entendiendo que esto puede implicar la imposibilidad de continuar utilizando la plataforma.\n\n" +
                "Estas solicitudes pueden realizarse a través de los canales de contacto oficiales de CargoExpress."
            )

            PrivacySectionTitle("Seguridad de la Información")
            PrivacyParagraph(
                "CargoSystems implementa medidas razonables de seguridad técnicas y organizativas para proteger los datos del usuario contra accesos no autorizados, pérdida o alteración, entre ellas:\n\n" +
                "• Autenticación mediante usuario y contraseña cifrada.\n" +
                "• Validaciones de formato en el registro (contraseña, teléfono, documentos de identidad).\n" +
                "• Registro de auditoría de cambios realizados sobre viajes, vehículos y conductores.\n\n" +
                "No obstante, el usuario reconoce que ningún sistema informático es completamente invulnerable, conforme a lo señalado en el Acuerdo de Servicio SaaS."
            )

            PrivacySectionTitle("Menores de Edad")
            PrivacyParagraph(
                "CargoExpress no está dirigido a menores de edad. El registro en la plataforma requiere que el usuario sea mayor de edad conforme a la legislación aplicable, tal como se establece en el Acuerdo de Servicio SaaS."
            )

            PrivacySectionTitle("Cambios en la Política de Privacidad")
            PrivacyParagraph(
                "CargoSystems podrá actualizar la presente Política de Privacidad cuando resulte necesario por razones legales, técnicas u operativas. Las modificaciones serán publicadas en la sección \"Política de Privacidad\" del website " +
                "y dentro de la aplicación móvil, y entrarán en vigencia desde su publicación."
            )

            PrivacySectionTitle("Ley Aplicable")
            PrivacyParagraph(
                "La presente Política de Privacidad se rige conforme a las leyes vigentes de la República del Perú en materia de protección de datos personales."
            )

            PrivacySectionTitle("Contacto")
            PrivacyParagraph(
                "Para consultas relacionadas con el tratamiento de datos personales, el usuario podrá comunicarse mediante los canales oficiales publicados en la sección \"Contacto\" del website."
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PrivacySectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 16.dp, bottom = 6.dp)
    )
}

@Composable
private fun PrivacyParagraph(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        lineHeight = 22.sp,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}
